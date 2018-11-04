package com.yali.intf

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, entity, extractUri, get, path, post, _}
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import com.yali.domain.service._
import com.yali.domain._
import com.yali.domain.payload._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.java8.time._
import io.circe.syntax._

import scala.concurrent.Future


class HttpServer(implicit val system: ActorSystem,
                 implicit val taskService: TaskService,
                 implicit val personService: PersonService,
                 implicit val countryService: CountryService,
                 implicit val languageService: LanguageService,
                 implicit val countryStateService: CountryStateService,
                 implicit val jwtToken: JwtToken) extends TimeInstances {

  val log = Logging(system, this.getClass.getName)

  val route: Route =
    authenticateOAuth2(realm = "secure site", myUserPassAuthenticator) { userName =>
      pathPrefix(JavaUUID / "tasks") { userId =>
        pathEnd {
          get {
            complete(taskService.findAll(userId))
          } ~ post {
            entity(um = as[TaskRequest]) { req => complete(taskService.create(userId, req))
            }
          }
        } ~ path(JavaUUID) { taskId => {
          post {
            entity(as[TaskRequest]) { req => complete(taskService.update(userId, taskId, req))
            }
          }
        }
        } ~ path(JavaUUID) { taskId =>
          complete(taskService.find(userId, taskId))
        }
      }
    } ~ pathPrefix("users") {
      path("login") {
        pathEnd {
          post {
            entity(as[LoginRequest]) { req => complete(personService.login(req)) }
          }
        }
      } ~ pathEnd {
        post {
          entity(as[RegistrationRequest]) { req => complete(personService.register(req)) }
        }
      }
    } ~ pathPrefix("admin") {
      pathPrefix("countries") {
        path(JavaUUID) { countryId =>
          get {
            complete(countryService.find(countryId))
          } ~ put {
            entity(as[CountryRequest]) { req => complete(countryService.update(countryId, req)) }
          } ~ delete {
            complete(countryService.delete(countryId))
          }
        } ~ path("languages") {
          complete("Done")
        } ~ pathEnd {
          get{
            complete(countryService.findAll())
          } ~ post {
            entity(as[CountryRequest]) { req => complete(countryService.create(req)) }
          }
        }
      } ~ pathPrefix("languages") {
        path(JavaUUID) { languageId =>
          get {
            complete(languageService.find(languageId))
          } ~ put {
            entity(as[LanguageRequest]) { req => complete(languageService.update(languageId, req)) }
          } ~ delete {
            complete(languageService.delete(languageId))
          }
        } ~ pathEnd {
          post {
            entity(as[LanguageRequest]) { req => complete(languageService.create(req)) }
          }
        }
      } ~ pathPrefix("states") {
        path(JavaUUID) { stateId =>
          get {
            complete(countryStateService.find(stateId))
          } ~ put {
            entity(as[CountryStateRequest]) { req => complete(countryStateService.update(stateId, req)) }
          } ~ delete {
            complete(countryStateService.delete(stateId))
          }
        } ~ pathEnd {
          post {
            entity(as[CountryStateRequest]) { req => complete(countryStateService.create(req)) }
          }
        }
      }

    }


  implicit def defaultExceptionHandler = ExceptionHandler {
    case notFound: NotFoundException =>
      extractUri {
        uri =>
          log.error("not Found error", notFound)
          val response = ErrorResponse(
            StatusCodes.NotFound.intValue,
            uri.toString(),
            notFound.getClass.getName,
            notFound.getMessage).asJson.noSpaces

          complete(HttpResponse(StatusCodes.InternalServerError, entity = response))
      }
    case validationEx: ValidationFailedException =>
      extractUri {
        uri =>
          val response = ErrorResponse(
            StatusCodes.BadRequest.intValue,
            uri.toString(),
            validationEx.getClass.getName,
            validationEx.errors.asJson.noSpaces).asJson.noSpaces

          complete(HttpResponse(StatusCodes.InternalServerError, entity = response))
      }
    case ex: Exception =>
      extractUri {
        uri =>
          log.error("unknown error", ex)
          val response = ErrorResponse(
            StatusCodes.InternalServerError.intValue,
            uri.toString(),
            ex.getClass.getName,
            ex.getMessage).asJson.noSpaces

          complete(HttpResponse(StatusCodes.InternalServerError, entity = response))
      }
  }

  def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p@Credentials.Provided(id) => {
        val result = jwtToken.find(id)
        println(result)
        Some(id)
      }
      case _ => None
    }

  def start()(implicit materializer: ActorMaterializer): Future[ServerBinding] =
    Http().bindAndHandle(route, "localhost", 8080)
}

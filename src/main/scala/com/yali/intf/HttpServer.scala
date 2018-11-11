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
import com.yali.domain._
import com.yali.domain.payload._
import com.yali.domain.resource.ResourceRegistry
import com.yali.domain.service._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.Future

class HttpServer(implicit val system: ActorSystem,
                 implicit val taskService: TaskService,
                 implicit val personService: PersonService,
                 implicit val countryService: CountryService,
                 implicit val languageService: LanguageService,
                 implicit val countryStateService: CountryStateService,
                 implicit val addressService: AddressService,
                 implicit val jwtToken: JwtToken){

  val log = Logging(system, this.getClass.getName)

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
    Http().bindAndHandle(new ResourceRegistry().route, "localhost", 8080)
}

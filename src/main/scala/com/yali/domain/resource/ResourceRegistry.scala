package com.yali.domain.resource

import akka.http.scaladsl.server.Directives.{JavaUUID, as, complete, delete, entity, get, path, pathEnd, pathPrefix, post, put, _}
import akka.http.scaladsl.server.directives.Credentials
import com.yali.domain.payload._
import com.yali.domain.service._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.java8.time._

class ResourceRegistry(implicit val personService: PersonService,
                       implicit val taskService: TaskService,
                       implicit val languageService: LanguageService,
                       implicit val addressService: AddressService,
                       implicit val countryStateService: CountryStateService,
                       implicit val countryService: CountryService,
                       implicit val jwtToken: JwtToken) extends TimeInstances {


  val taskRoute = pathPrefix(JavaUUID / "tasks") { userId =>
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

  val userRoute = pathPrefix("users") {
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
  }

  val languageRoutes = pathPrefix(JavaUUID / "languages") { countryId =>
    path(JavaUUID) { languageId =>
      get {
        complete(languageService.find(countryId, languageId))
      } ~ put {
        entity(as[LanguageRequest]) { req => complete(languageService.update(countryId, languageId, req)) }
      } ~ delete {
        complete(languageService.delete(countryId, languageId))
      }
    } ~ pathEnd {
      get {
        complete(languageService.findAllByCountry(countryId))
      } ~ post {
        entity(as[LanguageRequest]) { req => complete(languageService.create(countryId, req)) }
      }
    }
  }

  val addressRoute = pathPrefix("addresses") {
    path(JavaUUID) { addressId =>
      get {
        complete(addressService.find(addressId))
      } ~ put {
        entity(as[AddressRequest]) { req => complete(addressService.update(addressId, req)) }
      } ~ delete {
        complete(addressService.delete(addressId))
      }
    } ~ pathEnd {
      get {
        complete(addressService.findAll())
      } ~ post {
        entity(as[AddressRequest]) { req => complete(addressService.create(req)) }
      }
    }
  }

  val statesRoute = pathPrefix(JavaUUID / "states") { countryId =>
    path(JavaUUID) { stateId =>
      get {
        complete(countryStateService.find(countryId, stateId))
      } ~ put {
        entity(as[CountryStateRequest]) { req => complete(countryStateService.update(stateId, req)) }
      } ~ delete {
        complete(countryStateService.delete(stateId))
      }
    } ~ pathEnd {
      get {
        complete(countryStateService.find(countryId))
      } ~ post {
        entity(as[CountryStateRequest]) { req => complete(countryStateService.create(req)) }
      }
    }
  }

  val countriesRoute = pathPrefix("countries") {
    path(JavaUUID) { countryId =>
      get {
        complete(countryService.find(countryId))
      } ~ put {
        entity(as[CountryRequest]) { req => complete(countryService.update(countryId, req)) }
      } ~ delete {
        complete(countryService.delete(countryId))
      }
    } ~ pathEnd {
      get {
        complete(countryService.findAll())
      } ~ post {
        entity(as[CountryRequest]) { req => complete(countryService.create(req)) }
      }
    } ~ languageRoutes ~ statesRoute
  }

  val adminRoutes = pathPrefix("admin") {
    countriesRoute
  }

  val route =
  authenticateOAuth2(realm = "secure site", myUserPassAuthenticator) { userName =>
      taskRoute

    } ~ userRoute ~  adminRoutes ~  addressRoute

  def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p@Credentials.Provided(id) => {
        val result = jwtToken.find(id)
        println(result)
        Some(id)
      }
      case _ => None
    }
}

package com.yali.domain.service

import com.yali.domain.model.Person
import com.yali.domain.payload.{LoginRequest, RegistrationRequest}
import com.yali.intf.DBTestSupport
import com.yali.domain.{NotFoundException, ValidationFailedException}
import com.yali.domain.repository.PersonRepository
import org.mockito.Matchers._
import org.mockito.Mockito

class PersonServiceTest extends DBTestSupport {

    trait Fixture {
        implicit val mockPersonRepo = Mockito.mock(classOf[PersonRepository])
        implicit val webToken = Mockito.mock(classOf[JwtToken])
        val person = Person(
            username = "username",
            email = "email",
            password = "password")

        val registrationRequest = RegistrationRequest(
            username = "username",
            password = "P@ssw0rd",
            email = "e@mail.com")

        val personService = new PersonService()
    }

    "registration" should "not throw ValidationException" in new Fixture {
        Mockito.when(mockPersonRepo.create((any(classOf[Person])))(any())).thenReturn(person)
        val response = personService.register(registrationRequest)
    }
    it should "validate form" in new Fixture {
        assertThrows[ValidationFailedException] {
            personService.register(registrationRequest.copy(password = null))
        }
    }

    "login" should "validate person and return token" in new Fixture {
        Mockito.when(mockPersonRepo.findByEmailAndPassword(any(), any())(any())).thenReturn(Some(person))
        val response = personService.login(LoginRequest("email", "password"))
    }
    it should "throw exception if login fails" in new Fixture {
        Mockito.when(mockPersonRepo.findByEmailAndPassword(any(), any())(any())).thenReturn(None)
        assertThrows[NotFoundException] {
            personService.login(LoginRequest("email", "bad password"))
        }
    }

    "validateForm" should "validate email is not null" in new Fixture {
        personService.validateForm(registrationRequest.copy(email = null)).isInvalid shouldBe true
    }
    it should "validate username is not null" in new Fixture {
        personService.validateForm(registrationRequest.copy(username = null)).isInvalid shouldBe true
    }
    it should "validate username length <= 60" in new Fixture {
        personService.validateForm(registrationRequest.copy(username = "1" * 61)).isInvalid shouldBe true
    }
    it should "validate password is correct format" in new Fixture {
        personService.validateForm(registrationRequest.copy(password = "a")).isInvalid shouldBe true
    }
}



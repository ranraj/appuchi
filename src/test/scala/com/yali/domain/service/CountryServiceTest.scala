package com.yali.domain.service

import java.util.UUID

import com.yali.domain.ValidationFailedException
import com.yali.domain.model.Country
import com.yali.domain.payload.CountryRequest
import com.yali.domain.repository.CountryRepository
import com.yali.intf.DBTestSupport
import org.mockito.Matchers.{any, same}
import org.mockito.Mockito

class CountryServiceTest extends DBTestSupport {

  trait Fixture {
    val id = UUID.randomUUID()
    val countryRequest = CountryRequest(name = "India", code = "IND", currency = "INR", dollar_rate = 1.0, description = "Nothing")
    val country = Country(id = id, name = "India", code = "IND", currency = "INR", dollarRate = 1.0, description = "Nothing")
    implicit val countryRepo = Mockito.mock(classOf[CountryRepository])
    Mockito.when(countryRepo.create(any())(any())).thenReturn(country)
    Mockito.when(countryRepo.update(any())(any())).thenReturn(country)
    Mockito.when(countryRepo.find(same(id))(any())).thenReturn(Some(country))
    Mockito.when(countryRepo.delete(same(id))(any())).thenReturn(true)

    val countryService = new CountryService()
  }

  "CountryService" should "create country" in new Fixture {
    countryService.create(countryRequest).code shouldBe "IND"
  }
  it should "validate the request" in new Fixture {
    assertThrows[ValidationFailedException] {
      countryService.create(countryRequest.copy(description = null))
    }
  }
  it should "find country" in new Fixture {
    val response = countryService.find(country.id)
    response should not be null
    response should not be None
    response.map(a => a.name shouldEqual "India")
    response.map(a => a.currency shouldEqual "INR")
    response.map(a => a.dollar_rate shouldEqual 1.0)
    response.map(a => a.description shouldEqual "Nothing")
  }
  it should "validate on update country" in new Fixture {
    val response = countryService.update(id, countryRequest)
    response should not be null
    response.description shouldEqual "Nothing"

    assertThrows[ValidationFailedException] {
      countryService.update(id, countryRequest.copy(description = null))
    }
  }

  "CountryService validation" should "validate name" in new Fixture {
    countryService.validateRequest(countryRequest.copy(name = null)).isInvalid shouldBe true
  }

  it should "validate code min" in new Fixture {
    countryService.validateRequest(countryRequest.copy(code = "A")).isInvalid shouldBe true
  }
  it should "validate code max" in new Fixture {
    countryService.validateRequest(countryRequest.copy(code = "ABCDEFG")).isInvalid shouldBe true
  }
}

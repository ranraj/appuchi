package com.yali.domain.service

import java.util.UUID

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import Validator._
import com.yali.domain.ValidationFailedException
import com.yali.domain.model.{Country, CountryState, ID, Language}
import com.yali.domain.payload._
import com.yali.domain.repository.{CountryRepository, CountryStateRepository, LanguageRepository}
import scalikejdbc.DB


class CountryService(implicit countryRepo: CountryRepository) {
  import CountryParser._

  def validateRequest(req: CountryRequest): Validator.ValidationResult[CountryRequest] = {
    def validateCode(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
      notNull.andThen(_ => minLength(2)).andThen(_ => maxLength(4))

    def validateName(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
      notNull.andThen(_ => minLength(2))

    def validateCurrency(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
      notNull

    def validateDollarRate(implicit fieldValue: FieldValue[BigDecimal]): ValidationResult[BigDecimal] =
      notNull

    def validateDescription(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
      notNull.andThen(_ => minLength(5))

    (validateName(("name", req.name)),
      validateCode(("code", req.code)),
      validateCurrency(("currency", req.currency)),
      validateDollarRate(("dollar rate", req.dollar_rate)),
      validateDescription(("description", req.description))
    ).mapN(CountryRequest)
  }

  def create(request: CountryRequest): CountryResponse = {
    validateRequest(request) match {
      case Valid(value) =>
        toCountryResponse(DB localTx { implicit session =>
          countryRepo.create(createCountry(value))
        })
      case Invalid(errors) => throw new ValidationFailedException(errors.toList)
    }
  }

  def update(id: ID, request: CountryRequest): CountryResponse = DB localTx {
    implicit session => toCountryResponse(countryRepo.update(toCountry(id,request)))
  }

  def delete(id: ID): Boolean = DB localTx {
    implicit session => countryRepo.delete(id)
  }

  def find(countryId: ID): Option[CountryResponse] =
    DB readOnly { implicit session => countryRepo.find(countryId).map(toCountryResponse) }

  def findAll(code: String): List[CountryResponse] =
    DB readOnly { implicit session => countryRepo.findAll(code).map(toCountryResponse) }

  def findAll(): List[CountryResponse] =
    DB readOnly { implicit session => countryRepo.findAll().map(toCountryResponse) }

  private[this] def createCountry(req: CountryRequest): Country = new Country(
    name = req.name,
    code = req.code,
    currency = req.currency,
    dollarRate = req.dollar_rate,
    description = req.description
  )
}

object CountryParser{
  def toCountry(id: ID, country: CountryRequest) =
    Country(
      id = id,
      name = country.name,
      code = country.code,
      currency = country.currency,
      dollarRate = country.dollar_rate,
      description = country.description
    )

  def toCountryResponse(country: Country) =
    CountryResponse(
      id = country.id,
      name = country.name,
      code = country.code,
      currency = country.currency,
      dollar_rate = country.dollarRate,
      description = country.description
    )
}

class LanguageService(implicit languageRepo: LanguageRepository) {
  import LanguageParser._

  def validateRequest(req: LanguageRequest): Validator.ValidationResult[LanguageRequest] = {

    def validateName(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
      notNull.andThen(_ => minLength(2))

    def validateLocale(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
      notNull

    def validateCountryId(implicit fieldValue: FieldValue[UUID]): ValidationResult[UUID] =
      notNull

    (validateName(("name", req.name)),
      validateLocale(("locale", req.locale)),
      validateCountryId(("country_id", req.country_id))
    ).mapN(LanguageRequest)
  }

  def create(request: LanguageRequest): LanguageResponse = {
    validateRequest(request) match {
      case Valid(value) =>
        toLanguageResponse(DB localTx { implicit session =>
          languageRepo.create(createCountry(value))
        })
      case Invalid(errors) => throw new ValidationFailedException(errors.toList)
    }
  }

  def update(id: ID, request: LanguageRequest): LanguageResponse = DB localTx {
    implicit session => toLanguageResponse(languageRepo.update(toLanguage(id,request)))
  }

  def delete(id: ID): Boolean = DB localTx {
    implicit session => languageRepo.delete(id)
  }

  def find(countryId: ID): Option[LanguageResponse] =
    DB readOnly { implicit session => languageRepo.find(countryId).map(toLanguageResponse(_)) }

  def findAll(code: String): List[LanguageResponse] =
    DB readOnly { implicit session => languageRepo.findAll(code).map(toLanguageResponse(_)) }

  private[this] def createCountry(req: LanguageRequest): Language = new Language(
    name = req.name,
    locale = req.locale,
    countryId = Some(req.country_id)
  )
}

object LanguageParser{
  import CountryParser._

  def toLanguage(languageId: ID, req: LanguageRequest) =
    Language(
      id = languageId,
      name = req.name,
      locale = req.locale,
      countryId = Some(req.country_id)
    )

  def toLanguageResponse(language: Language) =
    LanguageResponse(
      id = language.id,
      name = language.name,
      locale = language.name,
      country = language.country.map(toCountryResponse(_))
    )
}

class CountryStateService(implicit repository: CountryStateRepository) {
  import CountryStateParser._

  def validateRequest(req: CountryStateRequest): Validator.ValidationResult[CountryStateRequest] = {

    def validateName(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
      notNull.andThen(_ => minLength(2))

    def validateCountryId(implicit fieldValue: FieldValue[UUID]): ValidationResult[UUID] =
      notNull

    (validateName(("name", req.name)),
      validateCountryId(("country_id", req.country_id))
    ).mapN(CountryStateRequest)
  }

  def create(request: CountryStateRequest): CountryStateResponse = {
    validateRequest(request) match {
      case Valid(value) =>
        toCountryStateResponse(DB localTx { implicit session =>
          repository.create(createEntity(value))
        })
      case Invalid(errors) => throw new ValidationFailedException(errors.toList)
    }
  }

  def update(id: ID, request: CountryStateRequest): CountryStateResponse = DB localTx {
    implicit session => toCountryStateResponse(repository.update(toCountryState(id,request)))
  }

  def delete(id: ID): Boolean = DB localTx {
    implicit session => repository.delete(id)
  }

  def find(countryId: ID): Option[CountryStateResponse] =
    DB readOnly { implicit session => repository.find(countryId).map(toCountryStateResponse(_)) }

  def findAll(code: String): List[CountryStateResponse] =
    DB readOnly { implicit session => repository.findAll(code).map(toCountryStateResponse(_)) }

  private[this] def createEntity(req: CountryStateRequest): CountryState = new CountryState(
    name = req.name,
    countryId = Some(req.country_id)
  )
}

object CountryStateParser{
  import CountryParser._

  def toCountryState(id: ID, req: CountryStateRequest) =
    CountryState(
      id = id,
      name = req.name,
      countryId = Some(req.country_id)
    )

  def toCountryStateResponse(entity: CountryState) =
    CountryStateResponse(
      id = entity.id,
      name = entity.name,
      country = entity.country.map(toCountryResponse(_))
    )
}
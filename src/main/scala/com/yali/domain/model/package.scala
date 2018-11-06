package com.yali.domain

import java.time.OffsetDateTime
import java.util.UUID

import scalikejdbc._

package object model {
  type ID = UUID

  case class Person(id: UUID = UUID.randomUUID(),
                    username: String,
                    email: String,
                    password: String,
                    primaryAddress: Option[PersonAddress] = None,
                    primaryPhone: Option[String] = None,
                    primaryEmail: Option[String] = None,
                    createdAt: OffsetDateTime = OffsetDateTime.now,
                    modifiedAt: OffsetDateTime = OffsetDateTime.now
                   )

  case class PersonAddress(street1: String,
                     street2: Option[String],
                     city: String,
                     state: String,
                     postalCode: String)

  case class Task(id: ID = UUID.randomUUID(),
                  userId: ID,
                  title: String,
                  details: String,
                  dueDate: Option[OffsetDateTime] = None,
                  complete: Boolean = false,
                  createdAt: OffsetDateTime = OffsetDateTime.now,
                  modifiedAt: OffsetDateTime = OffsetDateTime.now
                 )

  object Task extends SQLSyntaxSupport[Task] {
    override val tableName = "task"

    def apply(t: ResultName[Task])(rs: WrappedResultSet) =
      new Task(
        id = UUID.fromString(rs.string(t.id)),
        userId = UUID.fromString(rs.string(t.userId)),
        title = rs.string(t.title),
        details = rs.string(t.details),
        dueDate = rs.offsetDateTimeOpt(t.dueDate),
        complete = rs.boolean(t.complete))
  }

  case class Country(
                      id: UUID = UUID.randomUUID(),
                      name: String,
                      code: String,
                      currency: String,
                      dollarRate: BigDecimal,
                      description: String,
                    )

  object Country extends SQLSyntaxSupport[Country] {
    override val tableName = "app_country"

    def apply(country: ResultName[Country])(rs: WrappedResultSet) =
      new Country(
        id = UUID.fromString(rs.string(country.id)),
        name = rs.string(country.name),
        code = rs.string(country.code),
        currency = rs.string(country.currency),
        dollarRate = rs.bigDecimal(country.dollarRate),
        description = rs.string(country.description))
  }


  case class Language(
                       id: UUID = UUID.randomUUID(),
                       name: String,
                       locale: String,
                       countryId: Option[ID] = None,
                       country: Option[Country] = None
                     )


  object Language extends SQLSyntaxSupport[Language] {
    override val tableName = "app_language"

    def apply(language: ResultName[Language])(rs: WrappedResultSet): Language =
      new Language(
        id = UUID.fromString(rs.string(language.id)),
        name = rs.string(language.name),
        locale = rs.string(language.locale),
        countryId = rs.stringOpt(language.countryId).map(UUID.fromString(_))
      )

    def apply(l: ResultName[Language], c: ResultName[Country])(rs: WrappedResultSet): Language = {
      apply(l)(rs).copy(country = rs.stringOpt(c.id).map(_ => Country(c)(rs)))
    }
  }

  case class CountryState(
                           id: UUID = UUID.randomUUID(),
                           name: String,
                           countryId: ID,
                           country: Option[Country] = None
                         )

  object CountryState extends SQLSyntaxSupport[CountryState] {
    override val tableName = "app_country_state"

    def apply(state: ResultName[CountryState])(rs: WrappedResultSet): CountryState =
      new CountryState(
        id = UUID.fromString(rs.string(state.id)),
        name = rs.string(state.name),
        countryId = UUID.fromString(rs.string(state.countryId))
      )

    def apply(state: ResultName[CountryState], c: ResultName[Country])(rs: WrappedResultSet): CountryState = {
      apply(state)(rs).copy(country = rs.stringOpt(c.id).map(_ => Country(c)(rs)))
    }
  }

}

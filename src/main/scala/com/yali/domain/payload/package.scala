package com.yali.domain.payload

import java.time.OffsetDateTime
import java.util.UUID

import com.yali.domain.model.ID

final case class ErrorResponse(statusCode: Int,
                               uri: String,
                               exceptionType: String,
                               message: String)

final case class LoginRequest(email: String,
                              password: String)

final case class LoginResponse(username: String,
                               token: String)

final case class RegistrationRequest(username: String,
                                     password: String,
                                     email: String)

final case class RegistrationResponse(token: String)

final case class TaskRequest(title: String,
                             details: String,
                             dueDate: Option[OffsetDateTime] = None,
                             complete: Option[Boolean] = Some(false))

final case class TaskResponse(id: UUID,
                              title: String,
                              details: String,
                              dueDate: Option[OffsetDateTime] = None,
                              complete: Boolean)

final case class CountryRequest(name: String,
                                code: String,
                                currency: String,
                                dollar_rate: BigDecimal,
                                description: String,
                               )

final case class CountryResponse(id: UUID,
                                 name: String,
                                 code: String,
                                 currency: String,
                                 dollar_rate: BigDecimal,
                                 description: String,
                                )

final case class LanguageRequest(name: String,
                                 locale: String,
                                 country_id: UUID,
                                )

final case class LanguageResponse(id: UUID,
                                  name: String,
                                  locale: String,
                                  country: Option[CountryResponse]
                                 )

final case class LanguageResponseLazy(id: UUID,
                                  name: String,
                                  locale: String,
                                 )

final case class CountryStateRequest(name: String,
                                 country_id: UUID
                                )

final case class CountryStateResponse(id: UUID,
                                  name: String,
                                  country: Option[CountryResponse]
                                 )

final case class AddressRequest(
                                 line1: String,
                                 line2: Option[String] = None,
                                 landmark: Option[String] = None,
                                 city: String,
                                 countryId: ID,
                                 stateId: Option[ID] = None,
                                 livingPeriod: Option[Int] = None,
                                 latitude: Option[Double] = None,
                                 longitude: Option[Double] = None,
                                 zipCode: String,
                                 addressType: String
                               )
final case class AddressResponse(
                                 id : UUID,
                                 line1: String,
                                 line2: Option[String],
                                 landmark: Option[String],
                                 city: String,
                                 countryId: ID,
                                 country: Option[CountryResponse] = None,
                                 stateId: Option[ID],
                                 state: Option[ CountryStateResponse] = None,
                                 livingPeriod: Option[Int],
                                 latitude: Option[Double],
                                 longitude: Option[Double],
                                 zipCode: String,
                                 addressType: String
                               )

final case class BusinessTypeRequest(
                         name: String,
                         description: String,
                         parentId: Option[ID] = None,
                         displayName: String)

final case class BusinessTypeResponse(
                                      id: ID = UUID.randomUUID(),
                                      name: String,
                                      description: String,
                                      parentId: Option[ID] = None,
                                      parent: Option[BusinessTypeResponse] = None,
                                      displayName: String)
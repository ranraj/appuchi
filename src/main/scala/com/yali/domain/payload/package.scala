package com.yali.domain.payload

import java.time.OffsetDateTime
import java.util.UUID

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
                                 country_id: UUID,
                                )

final case class CountryStateResponse(id: UUID,
                                  name: String,
                                  country: Option[CountryResponse]
                                 )


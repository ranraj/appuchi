package com.yali.domain.service

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import Validator.{FieldValue, ValidationResult, maxLength, notNull}
import com.yali.domain.model.Person
import com.yali.domain.payload.{LoginRequest, LoginResponse, RegistrationRequest, RegistrationResponse}
import com.yali.domain.{NotFoundException, ValidationFailedException}
import com.yali.domain.repository.PersonRepository
import scalikejdbc.DB


class PersonService(implicit userRepo: PersonRepository, webToken: JwtToken) {

    def validateForm(req: RegistrationRequest): Validator.ValidationResult[RegistrationRequest] = {
        def validateUserName(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
            notNull.andThen(_ => maxLength(60))

        def validateEmail(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
            notNull.andThen(_ => Validator.validateEmail(fieldValue))

        def validatePassword(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
            notNull.andThen(_ => Validator.validatePassword(fieldValue))

        (validateUserName(("username", req.username)),
          validatePassword(("password", req.password)),
          validateEmail(("email", req.email))
        ).mapN(RegistrationRequest)
    }

    def login(user: LoginRequest): LoginResponse =
        DB localTx { implicit session =>
            val foundPerson =
                userRepo.findByEmailAndPassword(user.email, user.password)
                        .getOrElse(throw new NotFoundException("Invalid username or password"))

            LoginResponse(
                username = foundPerson.username,
                token = webToken.create(foundPerson.id.toString)
            )
        }

    def register(request: RegistrationRequest): RegistrationResponse = {
        validateForm(request) match {
            case Valid(value) =>
                val newPerson = DB localTx { implicit session =>
                    userRepo.create(createPerson(value))
                }
                RegistrationResponse(webToken.create(newPerson.id.toString))

            case Invalid(errors) => throw new ValidationFailedException(errors.toList)
        }
    }

    private[this] def createPerson(req: RegistrationRequest): Person = new Person(
        username = req.username,
        email = req.email,
        password = req.password)
}

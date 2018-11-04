package com.yali.domain.service

object Validator {

    import cats.data.{Validated, ValidatedNel}
    import cats.implicits._

    sealed trait DomainValidation {
        def errorMessage: String
    }

    type ValidationResult[A] = ValidatedNel[String, A]

    type FieldValue[A] = Tuple2[String, A]

    def notNull[A](implicit fieldValue: FieldValue[A]): ValidationResult[A] =
        if (fieldValue._2 != null) fieldValue._2.validNel
        else s"${fieldValue._1} is null".invalidNel

    def maxLength(length: Int)(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
        if (fieldValue._2.length <= length) fieldValue._2.validNel
        else s"${fieldValue._1} is $length".invalidNel

    def minLength(length: Int)(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
        if (fieldValue._2.length > length) fieldValue._2.validNel
        else s"${fieldValue._1} is $length".invalidNel

    def specialCharacters(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
        if (fieldValue._2.matches("^[a-zA-Z0-9]+$")) fieldValue._2.validNel
        else s"${fieldValue._1} cannot contain special characters.".invalidNel

    def validatePassword(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
        if (fieldValue._2.matches("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) fieldValue._2.validNel
        else s"${fieldValue._1} must be at least 8 characters long, including an uppercase and a lowercase letter, one number and one special character.".invalidNel

    def validateEmail(implicit fieldValue: FieldValue[String]): ValidationResult[String] = {
        val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
        fieldValue._2 match {
            case e if emailRegex.findFirstMatchIn(e).isDefined => Validated.valid(e)
            case _ => s"${fieldValue._1} is not valid".invalidNel
        }
    }

    def success[A](value: A): ValidationResult[A] = value.validNel
}

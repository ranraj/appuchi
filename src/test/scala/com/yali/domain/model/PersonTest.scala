package com.yali.domain.model

import io.circe.generic.auto._
import io.circe.java8.time._
import io.circe.parser.decode
import org.scalatest.{FlatSpec, Matchers}

class PersonTest extends FlatSpec with Matchers with TimeInstances {

    "create Person from JSON " should "be successful" in {
        val json = "{\"id\":\"70093fe5-5ac2-422b-87d8-c49084a250a4\",\"username\":\"A\",\"email\":\"b@c.com\",\"password\":\"D\",\"primaryAddress\":null,\"primaryPhone\":null,\"primaryEmail\":null,\"createdAt\":\"2017-11-27T17:09:44.584-10:00\",\"modifiedAt\":\"2017-11-27T17:09:44.584-10:00\"}"
        val maybePerson = decode[Person](json)

        maybePerson should not be None

        val person = maybePerson.right.get
        person.id should not be null
        person.email shouldEqual "b@c.com"
        person.password shouldEqual "D"
        person.username shouldEqual "A"
        person.primaryAddress shouldEqual None
        person.primaryAddress shouldEqual None
    }
}

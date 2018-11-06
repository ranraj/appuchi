package com.yali.domain.model

import io.circe.generic.auto._
import io.circe.java8.time._
import io.circe.parser.decode
import org.scalatest.{FlatSpec, Matchers}

//class AddressTest extends FlatSpec with Matchers with TimeInstances {
//
//    "create Address from JSON " should "be successful" in {
//        val json =
//          """{"id":"70093fe5-5ac2-422b-87d8-c49084a250a4",
//            |"line1":"First street","line2":"b@c.com",
//            |"landmark":"post office","countryId":"70093fe5-5ac2-422b-87d8-c49084a250a5",
//            |"stateId":"70093fe5-5ac2-422b-87d8-c49084a250a6",
//            |"livingPeriod":10,"latitude":100.22,
//            |"longitude":"30.22","zipCode":100200,
//            |"addressType":"PERMANENT"
//            |}""".stripMargin
//        val maybeAddress = decode[Address](json)
//
//        maybeAddress should not be None
//
//        val address = maybeAddress.right.get
//        address.id should not be null
//        address.line1 shouldEqual "First street"
//        address.stateId shouldEqual "70093fe5-5ac2-422b-87d8-c49084a250a6"
//        address.livingPeriod shouldEqual 10
//        address.addressType shouldEqual "PERMANENT"
//    }
//}

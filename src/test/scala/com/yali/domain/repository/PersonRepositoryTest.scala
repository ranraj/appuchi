package com.yali.domain.repository

import com.yali.domain.model.Person
import com.yali.intf.DBTestSupport
import com.yali.domain.repository.PersonRepository
import org.scalatest.{FlatSpec, Matchers}

class PersonRepositoryTest extends FlatSpec with Matchers with DBTestSupport {

    val personRepo = new PersonRepository()
    val email = "b@c.com"
    val password = "D"
    val person = Person(username = "A", email = email, password = password)

    "PersonRepository " should "create, update and retrieve Person" in {
        autoRollback { implicit session =>
            val newPerson = personRepo.create(person)
            newPerson should not be None

            val updated = personRepo.update(person.copy(email = "x@y.com"))
            updated.email shouldEqual "x@y.com"

            val maybePerson = personRepo.find(updated.id)
            maybePerson should not be None
            maybePerson.get.email shouldEqual "x@y.com"
        }
    }

    "PersonRepository " should "find by email + password" in {
        autoRollback { implicit session =>
            val newPerson = personRepo.create(person)
            newPerson should not be None

            personRepo.findByEmailAndPassword(email, password) should not be None
        }
    }

    "PersonRepository " should "find all" in {
        autoRollback { implicit session =>
            personRepo.create(person)
            personRepo.create(Person(email = "x@y.com", username = "B", password = "password"))

            personRepo.findAll.length shouldEqual 2
        }
    }
}

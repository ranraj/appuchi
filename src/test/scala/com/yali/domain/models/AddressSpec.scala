//package com.yali.domain.models
//
//import com.yali.domain.model.Address
//import scalikejdbc.specs2.mutable.AutoRollback
//import org.specs2.mutable._
//import scalikejdbc._
//
//
//class AddressSpec extends Specification {
//
//  "Address" should {
//
//    val a = Address.syntax("a")
//
//    "find by primary keys" in new AutoRollback {
//      val maybeFound = Address.find(null)
//      maybeFound.isDefined should beTrue
//    }
//    "find by where clauses" in new AutoRollback {
//      val maybeFound = Address.findBy(sqls.eq(a.id, null))
//      maybeFound.isDefined should beTrue
//    }
//    "find all records" in new AutoRollback {
//      val allResults = Address.findAll()
//      allResults.size should be_>(0)
//    }
//    "count all records" in new AutoRollback {
//      val count = Address.countAll()
//      count should be_>(0L)
//    }
//    "find all by where clauses" in new AutoRollback {
//      val results = Address.findAllBy(sqls.eq(a.id, null))
//      results.size should be_>(0)
//    }
//    "count by where clauses" in new AutoRollback {
//      val count = Address.countBy(sqls.eq(a.id, null))
//      count should be_>(0L)
//    }
//    "create new record" in new AutoRollback {
//      val created = Address.create(id = null, line1 = "MyString", countryId = null)
//      created should not beNull
//    }
//    "save a record" in new AutoRollback {
//      val entity = Address.findAll().head
//      // TODO modify something
//      val modified = entity
//      val updated = Address.save(modified)
//      updated should not equalTo(entity)
//    }
//    "destroy a record" in new AutoRollback {
//      val entity = Address.findAll().head
//      val deleted = Address.destroy(entity) == 1
//      deleted should beTrue
//      val shouldBeNone = Address.find(null)
//      shouldBeNone.isDefined should beFalse
//    }
//    "perform batch insert" in new AutoRollback {
//      val entities = Address.findAll()
//      entities.foreach(e => Address.destroy(e))
//      val batchInserted = Address.batchInsert(entities)
//      batchInserted.size should be_>(0)
//    }
//  }
//
//}

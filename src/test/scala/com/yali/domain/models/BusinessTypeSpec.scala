//package com.yali.domain.models
//
//import scalikejdbc.specs2.mutable.AutoRollback
//import org.specs2.mutable._
//import scalikejdbc._
//
//
//class BusinessTypeSpec extends Specification {
//
//  "BusinessType" should {
//
//    val bt = BusinessType.syntax("bt")
//
//    "find by primary keys" in new AutoRollback {
//      val maybeFound = BusinessType.find(null)
//      maybeFound.isDefined should beTrue
//    }
//    "find by where clauses" in new AutoRollback {
//      val maybeFound = BusinessType.findBy(sqls.eq(bt.id, null))
//      maybeFound.isDefined should beTrue
//    }
//    "find all records" in new AutoRollback {
//      val allResults = BusinessType.findAll()
//      allResults.size should be_>(0)
//    }
//    "count all records" in new AutoRollback {
//      val count = BusinessType.countAll()
//      count should be_>(0L)
//    }
//    "find all by where clauses" in new AutoRollback {
//      val results = BusinessType.findAllBy(sqls.eq(bt.id, null))
//      results.size should be_>(0)
//    }
//    "count by where clauses" in new AutoRollback {
//      val count = BusinessType.countBy(sqls.eq(bt.id, null))
//      count should be_>(0L)
//    }
//    "create new record" in new AutoRollback {
//      val created = BusinessType.create(id = null)
//      created should not beNull
//    }
//    "save a record" in new AutoRollback {
//      val entity = BusinessType.findAll().head
//      // TODO modify something
//      val modified = entity
//      val updated = BusinessType.save(modified)
//      updated should not equalTo(entity)
//    }
//    "destroy a record" in new AutoRollback {
//      val entity = BusinessType.findAll().head
//      val deleted = BusinessType.destroy(entity) == 1
//      deleted should beTrue
//      val shouldBeNone = BusinessType.find(null)
//      shouldBeNone.isDefined should beFalse
//    }
//    "perform batch insert" in new AutoRollback {
//      val entities = BusinessType.findAll()
//      entities.foreach(e => BusinessType.destroy(e))
//      val batchInserted = BusinessType.batchInsert(entities)
//      batchInserted.size should be_>(0)
//    }
//  }
//
//}

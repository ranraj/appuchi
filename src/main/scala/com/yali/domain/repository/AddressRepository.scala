package com.yali.domain.repository

import java.util.UUID

import com.yali.domain.model.{Address, AddressType, ID}
import scalikejdbc._

class AddressRepository extends RepositoryHelper{
  val a = Address.syntax("a")

  def find(id: UUID)(implicit session: DBSession): Option[Address] = {
    withSQL {
      select.from(Address as a).where.eq(a.id, id)
    }.map(Address(a.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[Address] = {
    withSQL(select.from(Address as a)).map(Address(a.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(Address as a)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[Address] = {
    withSQL {
      select.from(Address as a).where.append(where)
    }.map(Address(a.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[Address] = {
    withSQL {
      select.from(Address as a).where.append(where)
    }.map(Address(a.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(Address as a).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(entity: Address)(implicit session: DBSession): Address = {
    sql"""insert into ${Address.table}
         |  (
         |      id,
         |      line1,
         |      line2,
         |      landmark,
         |      city,
         |      country_id,
         |      state_id,
         |      living_period,
         |      latitude,
         |      longitude,
         |      zip_code,
         |      address_type) values (
         |      ${entity.id},
         |      ${entity.line1},
         |      ${entity.line2},
         |      ${entity.landmark},
         |      ${entity.city},
         |      ${entity.countryId},
         |      ${entity.stateId},
         |      ${entity.livingPeriod},
         |      ${entity.latitude},
         |      ${entity.longitude},
         |      ${entity.zipCode},
         |      ${AddressType.toValue(entity.addressType)}::address_type_enum
         | )""".stripMargin.update.apply()

    mustExist(find(entity.id))
  }
  def update(entity: Address)(implicit session: DBSession): Address = {
    sql"""update  ${Address.table} set
         |      line1 = ${entity.line1},
         |      line2 = ${entity.line2},
         |      landmark = ${entity.landmark},
         |      city = ${entity.city},
         |      country_id = ${entity.countryId},
         |      state_id = ${entity.stateId},
         |      living_period = ${entity.livingPeriod},
         |      latitude = ${entity.latitude},
         |      longitude = ${entity.longitude},
         |      zip_code = ${entity.zipCode},
         |      address_type = ${AddressType.toValue(entity.addressType)}::address_type_enum
         |      where id = ${entity.id}
         |  """.stripMargin.update.apply()

    mustExist(find(entity.id))
  }
  def delete(id: ID)(implicit session: DBSession): Boolean =
    sql"delete from ${Address.table} where id = $id".update().apply() > 0
}

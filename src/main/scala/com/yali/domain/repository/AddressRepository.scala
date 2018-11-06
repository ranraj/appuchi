package com.yali.domain.repository

import java.util.UUID

import com.yali.domain.model.{Address, AddressTypeEnum, ID}
import com.yali.domain.model.Address.column
import com.yali.domain.model.AddressTypeEnum.AddressTypeEnum
import scalikejdbc._

class AddressRepository extends RepositoryHelper{
  val a = Address.syntax("a")

  implicit lazy val uuidFactory = ParameterBinderFactory[UUID] {
    value => (stmt, idx) => stmt.setObject(idx, value)
  }

  implicit lazy val addressTypeFactory = ParameterBinderFactory[AddressTypeEnum] {
    value => (stmt, idx) => stmt.setObject(idx, value)
  }

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
  val addressType = "Permanent"
  def create(entity: Address)(implicit session: DBSession): Address = {
    sql"""insert into ${Address.table}
         |  (
         |      id,
         |      line1,
         |      line2,
         |      landmark,
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
         |      ${entity.countryId},
         |      ${entity.stateId},
         |      ${entity.livingPeriod},
         |      ${entity.latitude},
         |      ${entity.longitude},
         |      ${entity.zipCode},
         |      ${AddressTypeEnum.getValue(entity.addressType)}::address_type_enum
         | )""".stripMargin.update.apply()

    mustExist(find(entity.id))
  }

  def batchInsert(entities: collection.Seq[Address])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'id -> entity.id,
        'line1 -> entity.line1,
        'line2 -> entity.line2,
        'landmark -> entity.landmark,
        'countryId -> entity.countryId,
        'stateId -> entity.stateId,
        'livingPeriod -> entity.livingPeriod,
        'latitude -> entity.latitude,
        'longitude -> entity.longitude,
        'zipCode -> entity.zipCode,
        'addressType -> entity.addressType))
    SQL("""insert into address(
      id,
      line1,
      line2,
      landmark,
      country_id,
      state_id,
      living_period,
      latitude,
      longitude,
      zip_code,
      address_type
    ) values (
      {id},
      {line1},
      {line2},
      {landmark},
      {countryId},
      {stateId},
      {livingPeriod},
      {latitude},
      {longitude},
      {zipCode},
      {addressType}
    )""").batchByName(params: _*).apply[List]()
  }

//  def save(entity: Address)(implicit session: DBSession): Address = {
  ////    withSQL {
  ////      update(Address).set(
  ////        (column.id, ParameterBinder(entity.id, (ps, i) => ps.setObject(i, entity.id))),
  ////        column.line1 -> entity.line1,
  ////        column.line2 -> entity.line2,
  ////        column.landmark -> entity.landmark,
  ////        (column.countryId, ParameterBinder(entity.countryId, (ps, i) => ps.setObject(i, entity.countryId))),
  ////        (column.stateId, ParameterBinder(entity.stateId, (ps, i) => ps.setObject(i, entity.stateId))),
  ////        column.livingPeriod -> entity.livingPeriod,
  ////        column.latitude -> entity.latitude,
  ////        column.longitude -> entity.longitude,
  ////        column.zipCode -> entity.zipCode,
  ////        column.addressType -> entity.addressType
  ////      ).where.eq(column.id, entity.id)
  ////    }.update.apply()
  ////    entity
  ////  }

  def destroy(entity: Address)(implicit session: DBSession): Int = {
    withSQL { delete.from(Address).where.eq(column.id, entity.id) }.update.apply()
  }
}

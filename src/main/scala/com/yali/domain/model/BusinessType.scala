package com.yali.domain.model

import java.util.UUID

import com.yali.domain.repository.RepositoryHelper
import scalikejdbc._

case class BusinessType(
  id: ID = UUID.randomUUID(),
  name: String,
  description: String,
  parentId: Option[ID] = None,
  parent: Option[BusinessType] = None,
  displayName: String) {

  def save()(implicit session: DBSession): BusinessType = BusinessType.save(this)(session)

  def destroy()(implicit session: DBSession): Int = BusinessType.destroy(this)(session)

}


object BusinessType extends SQLSyntaxSupport[BusinessType] with RepositoryHelper{

  override val schemaName = Some("public")

  override val tableName = "business_type"

  override val columns = Seq("id", "name", "description", "parent_id", "display_name")

  def apply(bt: SyntaxProvider[BusinessType])(rs: WrappedResultSet): BusinessType = apply(bt.resultName)(rs)
  def apply(bt: ResultName[BusinessType])(rs: WrappedResultSet): BusinessType = new BusinessType(
    id = UUID.fromString(rs.string(bt.id)),
    name = rs.get(bt.name),
    description = rs.get(bt.description),
    parentId = rs.stringOpt(bt.parentId).map(UUID.fromString(_)),
    displayName = rs.get(bt.displayName)
  )

  val bt = BusinessType.syntax("bt")

  override val autoSession = AutoSession

  def find(id: ID)(implicit session: DBSession): Option[BusinessType] = {
    withSQL {
      select.from(BusinessType as bt).where.eq(bt.id, id)
    }.map(BusinessType(bt.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[BusinessType] = {
    withSQL(select.from(BusinessType as bt)).map(BusinessType(bt.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(BusinessType as bt)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[BusinessType] = {
    withSQL {
      select.from(BusinessType as bt).where.append(where)
    }.map(BusinessType(bt.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[BusinessType] = {
    withSQL {
      select.from(BusinessType as bt).where.append(where)
    }.map(BusinessType(bt.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(BusinessType as bt).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
              req: BusinessType)(implicit session: DBSession): BusinessType = {
    withSQL {
      insert.into(BusinessType).namedValues(
        (column.id, ParameterBinder(req.id, (ps, i) => ps.setObject(i, req.id))),
        column.name -> req.name,
        column.description -> req.description,
        (column.parentId, ParameterBinder(req.parentId, (ps, i) => ps.setObject(i, req.parentId))),
        column.displayName -> req.displayName
      )
    }.update.apply()

    mustExist(find(req.id))
  }

  def batchInsert(entities: collection.Seq[BusinessType])(implicit session: DBSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'id -> entity.id,
        'name -> entity.name,
        'description -> entity.description,
        'parentId -> entity.parentId,
        'displayName -> entity.displayName))
    SQL("""insert into business_type(
      id,
      name,
      description,
      parent_id,
      display_name
    ) values (
      {id},
      {name},
      {description},
      {parentId},
      {displayName}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: BusinessType)(implicit session: DBSession): BusinessType = {
    withSQL {
      update(BusinessType).set(
        (column.id, ParameterBinder(entity.id, (ps, i) => ps.setObject(i, entity.id))),
        column.name -> entity.name,
        column.description -> entity.description,
        (column.parentId, ParameterBinder(entity.parentId, (ps, i) => ps.setObject(i, entity.parentId))),
        column.displayName -> entity.displayName
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: BusinessType)(implicit session: DBSession): Int = {
    withSQL { delete.from(BusinessType).where.eq(column.id, entity.id) }.update.apply()
  }

}

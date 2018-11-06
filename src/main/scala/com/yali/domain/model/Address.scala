package com.yali.domain.model

import java.util.UUID

import scalikejdbc._

sealed trait AddressType
object AddressType {
  case object PERMANENT extends AddressType
  case object PRESENT extends AddressType

  def toValue(addressType: AddressType): String = addressType match {
    case PERMANENT => "Permanent"
    case PRESENT => "Present"
  }
  def fromValue(value: String): AddressType = value match {
    case "Permanent" => PERMANENT
    case "Present" => PRESENT
  }
}

case class Address(
                    id: ID = UUID.randomUUID(),
                    line1: String,
                    line2: Option[String] = None,
                    landmark: Option[String] = None,
                    countryId: ID,
                    stateId: Option[ID] = None,
                    livingPeriod: Option[Int] = None,
                    latitude: Option[Double] = None,
                    longitude: Option[Double] = None,
                    zipCode: String,
                    addressType: AddressType) {
}


object Address extends SQLSyntaxSupport[Address] {

  override val schemaName = Some("public")

  override val tableName = "address"

  override val columns = Seq("id", "line1", "line2", "landmark", "country_id", "state_id", "living_period", "latitude", "longitude", "zip_code", "address_type")

  implicit lazy val uuidFactory = ParameterBinderFactory[UUID] {
    value => (stmt, idx) => stmt.setObject(idx, value)
  }
  def apply(a: SyntaxProvider[Address])(rs: WrappedResultSet): Address = apply(a.resultName)(rs)
  def apply(a: ResultName[Address])(rs: WrappedResultSet): Address = new Address(
    id = UUID.fromString(rs.string(a.id)),
    line1 = rs.get(a.line1),
    line2 = rs.get(a.line2),
    landmark = rs.get(a.landmark),
    countryId = UUID.fromString(rs.string(a.countryId)),
    stateId = rs.stringOpt(a.stateId).map(UUID.fromString(_)),
    livingPeriod = rs.get(a.livingPeriod),
    latitude = rs.get(a.latitude),
    longitude = rs.get(a.longitude),
    zipCode = rs.get(a.zipCode),
    addressType = AddressType.fromValue(rs.string(a.addressType))
  )
}

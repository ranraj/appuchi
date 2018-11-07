package com.yali.domain.service

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import com.yali.domain.model.{Address, AddressType, ID}
import com.yali.domain.payload._
import com.yali.domain.repository.AddressRepository
import com.yali.domain.service.Validator._
import com.yali.domain.ValidationFailedException
import scalikejdbc.DB


class AddressService(implicit addressRepo: AddressRepository) {
    import AddressParser._

    def validateRequest(req: AddressRequest): ValidationResult[AddressRequest] = {
        def validateLine1(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
            notNull.andThen(_ => minLength(4))

        def validateAddressType(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
            notNull

        (validateLine1(("lin1", req.line1)),
          success(req.line2),
          noneValidation(req.landmark),
          notNull(("city", req.city)),
          notNull(("country", req.countryId)),
          notNull(("state", req.stateId)),
          notNull(("living period", req.livingPeriod)),
          noneValidation(req.latitude),
          noneValidation(req.longitude),
          notNull(("zip code", req.zipCode)),
          validateAddressType(("address type", req.addressType))
        ).mapN(AddressRequest)
    }

    def create(request: AddressRequest): AddressResponse = {
        validateRequest(request) match {
            case Valid(value) =>
                toAddressResponse(DB localTx { implicit session =>
                    addressRepo.create(createAddress(value))
                })
            case Invalid(errors) => throw new ValidationFailedException(errors.toList)
        }
    }

    def update(id: ID, request: AddressRequest): AddressResponse = DB localTx {
        implicit session =>  validateRequest(request) match {
            case Valid(value) =>
                toAddressResponse(addressRepo.update(toAddress(id,value)))
            case Invalid(errors) => throw new ValidationFailedException(errors.toList)
        }
    }

    def delete(id: ID): Boolean = DB localTx {
        implicit session => addressRepo.delete(id)
    }

    def find(addressId: ID): Option[AddressResponse] =
        DB readOnly { implicit session => addressRepo.find(addressId).map(toAddressResponse) }

    def findAll(): List[AddressResponse] =
        DB readOnly { implicit session => addressRepo.findAll().map(toAddressResponse) }

    private[this] def createAddress(req: AddressRequest): Address = new Address(
        line1 = req.line1,
        line2 = req.line2,
        landmark = req.landmark,
        city = req.city,
        countryId = req.countryId,
        stateId = req.stateId,
        livingPeriod = req.livingPeriod,
        latitude = req.latitude,
        longitude = req.longitude,
        zipCode = req.zipCode,
        addressType = AddressType.fromValue(req.addressType)
    )

}

object AddressParser{
    def toAddress(id: ID, req: AddressRequest) =
        Address(
            id = id,
            line1 = req.line1,
            line2 = req.line2,
            landmark = req.landmark,
            city = req.city,
            stateId = req.stateId,
            countryId = req.countryId,
            livingPeriod = req.livingPeriod,
            latitude = req.latitude,
            longitude = req.longitude,
            zipCode = req.zipCode,
            addressType = AddressType.fromValue(req.addressType)
        )
    def toAddressResponse(entity: Address) =
        AddressResponse(
            id = entity.id,
            line1 = entity.line1,
            line2 = entity.line2,
            city = entity.city,
            landmark = entity.landmark,
            stateId = entity.stateId,
            countryId = entity.countryId,
            livingPeriod = entity.livingPeriod,
            latitude = entity.latitude,
            longitude = entity.longitude,
            zipCode = entity.zipCode,
            addressType = entity.addressType.toString
        )
}
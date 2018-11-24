package com.yali.domain.service

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import com.yali.domain.model.{BusinessType, ID}
import com.yali.domain.payload._
import com.yali.domain.service.Validator._
import com.yali.domain.ValidationFailedException
import scalikejdbc.DB


class BusinessTypeService() {
    import BusinessTypeParser._

    def validateRequest(req: BusinessTypeRequest): ValidationResult[BusinessTypeRequest] = {
        def validateName(implicit fieldValue: FieldValue[String]): ValidationResult[String] =
            notNull.andThen(_ => minLength(4))

        (validateName(("name", req.name)),
          notNull(("description",req.description)),
          noneValidation(req.parentId),
          notNull(("display-name", req.displayName))
        ).mapN(BusinessTypeRequest)
    }


    def create(request: BusinessTypeRequest): BusinessTypeResponse = {
        validateRequest(request) match {
            case Valid(value) =>
                toBusinessTypeResponse(DB localTx { implicit session =>
                    createBusinessType(value).save()
                })
            case Invalid(errors) => throw new ValidationFailedException(errors.toList)
        }
    }

//    def update(id: ID, request: AddressRequest): AddressResponse = DB localTx {
//        implicit session =>  validateRequest(request) match {
//            case Valid(value) =>
//                toAddressResponse(addressRepo.update(toAddress(id,value)))
//            case Invalid(errors) => throw new ValidationFailedException(errors.toList)
//        }
//    }
//
//    def delete(id: ID): Boolean = DB localTx {
//        implicit session => addressRepo.delete(id)
//    }
//
    def find(addressId: ID): Option[BusinessType] =
        DB readOnly { implicit session => BusinessType.find(addressId) }
//
//    def findAll(): List[AddressResponse] =
//        DB readOnly { implicit session => addressRepo.findAll().map(toAddressResponse) }

    private[this] def createBusinessType(req: BusinessTypeRequest) = new BusinessType(
        name = req.name,
        description = req.description,
        parentId = req.parentId,
        displayName = req.displayName
    )

}
object BusinessTypeParser {
    def toBusinessType(id: ID, req: BusinessTypeRequest) =
        BusinessType(
            id = id,
            name = req.name,
            description = req.description,
            parentId = req.parentId,
            displayName = req.displayName
        )

    def toBusinessTypeResponse(entity: BusinessType): BusinessTypeResponse =
        BusinessTypeResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            parentId = entity.parentId,
            //parent = entity.parentId.map(toBusinessTypeResponse(_)),
            displayName = entity.displayName,
        )
}
package utils

import play.api.libs.json._
import java.util.UUID
import scala.util.{Failure, Success, Try}
import scala.util.Failure
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import scala.util.Success
import play.api.data.validation.ValidationError

/**
 *
 * Quick helpers for JSON formatters
 *
 */
object Formatters {
  def uuidFormatter : Format[UUID] = new Format[UUID] {
    def writes(uuid: UUID): JsString = JsString(uuid.toString)
    def reads(value: JsValue): JsResult[UUID] = value match {
      case JsString(x) => Try(UUID.fromString(x)) match {
        case Success(uuid) => JsSuccess(uuid)
        case Failure(msg) => JsError( __ \ 'UUID, ValidationError("validate.error.invalidUUID", msg) )
      }
      case _ => JsError( __ \ 'UUID, ValidationError("validate.error.invalidUUID", "Missing UUID String") )
    }
  }

  implicit val uuidJsonFormatter = uuidFormatter
}

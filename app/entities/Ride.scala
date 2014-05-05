package entities

import java.util.UUID
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json._
import utils.Formatters.uuidJsonFormatter

/**
 * Model to represent a Ride
 */

case class Ride(
  id:         Option[Long],
  clientId:   UUID,
  driverId:   UUID,
  startTime:  Long,
  lat:        Float,
  lon:        Float,
  fare:       Double,
  distance:   Double,
  rating:     Int
)

object Ride {

  implicit val rideJsonFormatter = Json.format[Ride]

}


/**
 * Here we define the DB table to Ride object Mapping (basic ORM stuff)
 */
class RideTable extends Table[Ride]("rides"){
  def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def clientId  = column[UUID]("client_id")
  def driverId  = column[UUID]("driver_id")
  def startTime = column[Long]("start_time")
  def lat       = column[Float]("lat")
  def lon       = column[Float]("lon")
  def fare      = column[Double]("fare")
  def distance  = column[Double]("distance")
  def rating    = column[Int]("rating")

  def * = id.? ~
  clientId ~
  driverId ~
  startTime ~
  lat ~
  lon ~
  fare ~
  distance ~
  rating <> (Ride.apply _, Ride.unapply _)

  def autoInc = * returning id
}

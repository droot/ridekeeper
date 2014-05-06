package controllers

import persistence.RidePersistenceComponent
import services.RideServiceComponent
import play.api.mvc._
import play.api.libs.json._
import entities.Ride.{rideJsonFormatter}

/**
 *
 * Ride Controller implements the REST interface
 *
 */

class Rides
  extends Controller
  with RidePersistenceComponent
  with RideServiceComponent
{

  val ridePersistence = RidePersistenceImpl
  val rideService = RideServiceImpl


  def create = Action(parse.json) {
    request =>
     request.body.validate[entities.Ride].fold(
      valid = ride => {
        rideService.create(ride).map {
          id => Created(Json.obj("id" -> id))
        }.getOrElse(InternalServerError(s"Failed to create Ride"))
      },
      invalid = err => BadRequest(s"Error in validating request: $err")
     )
  }

  def getById(id: Int) = Action {

    rideService.getById(id).map {
      ride =>
      Ok(Json.toJson(ride))
    }.getOrElse(NotFound(Json.obj("message" -> s"Ride not found with id=$id")))

  }

  def getRideCount(t1: Long = 0, t2: Long = 0) = Action {

    var range: Map[String, Long] = Map()
    if (t1 > 0) range = range + ("t1" -> t1)
    if (t2 > 0) range = range + ("t2" -> t2)

    Ok(Json.obj("count" -> rideService.getRideCount(range)))
  }

  def getClientCount(t1: Long = 0, t2: Long = 0) = Action {

    var range: Map[String, Long] = Map()
    if (t1 > 0) range = range + ("t1" -> t1)
    if (t2 > 0) range = range + ("t2" -> t2)

    Ok(Json.obj("count" -> rideService.getClientCount(range)))
  }

  def getAvgFare(latMin: Float,
                 lonMin: Float,
                 latMax: Float,
                 lonMax: Float,
                 t1: Long = 0,
                 t2: Long = 0) = Action {

    var range: Map[String, Long] = Map()
    if (t1 > 0) range = range + ("t1" -> t1)
    if (t2 > 0) range = range + ("t2" -> t2)

    val bounds = Map(
      "latMin" -> latMin,
      "lonMin" -> lonMin,
      "latMax" -> latMax,
      "lonMax" -> lonMax
    )

    Ok(Json.obj("count" -> rideService.getAvgFare(bounds, range)))
  }

  def getMilesPerClient() = Action {
    Ok(Json.toJson(rideService.getMilesPerClient()))
  }

}

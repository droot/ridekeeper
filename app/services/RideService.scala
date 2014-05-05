package services

import entities._
import persistence.RidePersistenceComponent
import java.util.UUID
import play.api.Play.current
import play.api.cache.Cache
import play.api.Logger

/**
 *
 * Ride Service Implementation
 *
 */

trait RideServiceComponent {
  this: RidePersistenceComponent =>

  val rideService: RideService

  def cacheKey(prefix: String, range: Map[String, Long]) = {

    var key = prefix

    if (range.contains("t1")) key += ".t1.%s".format(range.get("t1"))
    if (range.contains("t2")) key += ".t2.%s".format(range.get("t2"))

    key
  }

  trait RideService {

    def create(r: Ride): Option[Long]

    def getById(id: Long): Option[Ride]

    def delete(id: Long): Unit

    def deleteAll(): Unit

    def getRideCount(range: Map[String, Long] = Map()): Long

    def getClientCount(range: Map[String, Long] = Map()): Long

    def getMilesPerClient(range: Map[String, Long] = Map()): List[(UUID, Double)]

    def getMedianRating(driverId: UUID, range: Map[String, Long] = Map()): Option[Double]

    def getAvgFare(bounds: Map[String, Float], range: Map[String, Long] = Map()): Option[Double]
  }

  object RideServiceImpl extends RideService{

    var cacheKeys: List[String] = Nil

    def create(r: Ride): Option[Long] = {
      val id = ridePersistence.create(r)
      //cache invalidation: TODO: make it more granular
      cacheKeys.foreach(k => Cache.remove(k))
      id
    }

    def getById(id: Long): Option[Ride] = {
      val key = s"ride.$id"
      Cache.getOrElse(key){
        cacheKeys = key :: cacheKeys
        ridePersistence.getById(id)
      }
    }

    def delete(id: Long): Unit = {
      ridePersistence.delete(id)
      cacheKeys.foreach(k => Cache.remove(k))
    }

    def deleteAll(): Unit = {
      ridePersistence.deleteAll()
      cacheKeys.foreach(k => Cache.remove(k))
    }

    def getRideCount(range: Map[String, Long] = Map()): Long = {
      val key = cacheKey("ride.count", range)
      Cache.getOrElse(key) {
        cacheKeys = key :: cacheKeys
        ridePersistence.getRideCount(range)
      }
    }

    def getClientCount(range: Map[String, Long] = Map()): Long = {
      val key = cacheKey("client.count", range)
      Cache.getOrElse(key) {
        cacheKeys = key :: cacheKeys
        ridePersistence.getClientCount(range)
      }
    }

    def getMilesPerClient(range: Map[String, Long] = Map()) = {
      val key = cacheKey("miles.per.client", range)
      Cache.getOrElse(key) {
        cacheKeys = key :: cacheKeys
        ridePersistence.getMilesPerClient(range)
      }
    }

    def getMedianRating(driverId: UUID, range: Map[String, Long] = Map()) = {
      val key = cacheKey(s"median.rating.${driverId}", range)
      Cache.getOrElse(key){
        cacheKeys = key :: cacheKeys
        ridePersistence.getMedianRating(driverId, range)
      }
    }

    def getAvgFare(bounds: Map[String, Float], range: Map[String, Long] = Map()): Option[Double] = {

      val key = cacheKey("avg.fare.%s.%s.%s.%s".format(bounds.get("latMin"),
        bounds.get("lonMin"),
        bounds.get("latMax"),
        bounds.get("lonMax")),range)

      Cache.getOrElse(key){
        cacheKeys = key :: cacheKeys
        ridePersistence.getAvgFare(bounds, range)
      }
    }
  }

}

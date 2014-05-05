package persistence

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.Play.current
import play.api.Logger

import entities.{RideTable, Ride}
import java.util.UUID

/**
 *
 * Ride Persistence Layer
 *
 */
trait RidePersistenceComponent {

  val ridePersistence:RidePersistence

  trait RidePersistence {

    def create(r: Ride): Option[Long]

    def getById(id: Long): Option[Ride]

    def getRideCount(range: Map[String, Long]): Int

    def getClientCount(range: Map[String, Long]): Int

    def getMilesPerClient(range: Map[String, Long]): List[(UUID, Double)]

    def getMedianRating(driverId: UUID, range: Map[String, Long]): Option[Double]

    def getAvgFare(bounds: Map[String, Float], range: Map[String, Long]): Option[Double]

    def delete(id: Long): Unit

    def deleteAll(): Unit
  }

  /**
   * One implementation of persistence layer
   */

  object RidePersistenceImpl extends RidePersistence {

    private lazy val rideTable = new RideTable

    def create(r: Ride): Option[Long] = DB.withSession {
      implicit s: Session =>
        Some(rideTable.autoInc.insert(r))
    }

    private def defaultQuery(range: Map[String, Long] = Map()) = {

      val q = Query(rideTable)

      if (range.contains("t1"))
        q.filter(_.startTime >= range.get("t1"))

      if (range.contains("t2"))
        q.filter(_.startTime <= range.get("t2"))
      q
    }

    def getById(id: Long): Option[Ride] = DB.withSession {
      implicit s: Session =>
      Query(rideTable).filter(_.id === id).firstOption
    }

    def getRideCount(range: Map[String, Long]): Int = DB.withSession {
      implicit s: Session =>
        defaultQuery(range).list.length
    }

    def getClientCount(range: Map[String, Long]): Int = DB.withSession {
      implicit s: Session =>
        defaultQuery(range).groupBy(_.clientId).map {case (clientId, c) => 1 }.list.length
    }

    def getMilesPerClient(range: Map[String, Long]): List[(UUID, Double)] = DB.withSession {
      implicit s: Session =>
        defaultQuery(range).groupBy(_.clientId).map {case (clientId, c) => (clientId, c.map(_.distance).sum.get)}.list
    }

    def getMedianRating(driverId: UUID, range: Map[String, Long]): Option[Double] = DB.withSession {
      implicit s: Session =>
        val ratings = defaultQuery(range).filter(_.driverId === driverId).sortBy(_.rating).list.map(_.rating)

        val len = ratings.length
        len match {
          case l if l == 0 =>
            None
          case l if l%2 == 0 =>
            Some((ratings(l/2) + ratings(l/2 + 1))/2.0)
          case _ =>
            Some(ratings(len/2)*1.0)
        }
    }

    def getAvgFare(bounds: Map[String, Float], range: Map[String, Long]): Option[Double] = DB.withSession {
      implicit s: Session =>

        val fares = defaultQuery(range).
          filter(_.lat >= bounds.get("latMin")).
          filter(_.lon >= bounds.get("lonMin")).
          filter(_.lat <= bounds.get("latMax")).
          filter(_.lon <= bounds.get("lonMax")).
          map(_.fare).list

        if (fares.nonEmpty)
          Some(fares.sum/fares.length)
        else
          None
    }


    def delete(id: Long): Unit = DB.withSession {
      implicit s:Session =>
        Query(rideTable).filter(_.id === id).delete(s)
    }

    def deleteAll(): Unit = DB.withSession {
      implicit s:Session =>
        Query(rideTable).delete(s)
    }
  }

}

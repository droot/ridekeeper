//package controllers

import org.specs2.mutable._
import persistence.RidePersistenceComponent
import services.RideServiceComponent
import org.specs2.mock.Mockito
import play.api.test.{FakeHeaders, FakeRequest, WithApplication}
import entities.Ride
import java.util.UUID
import com.github.nscala_time.time.Imports._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.FakeHeaders

/**
 * Created by sarora on 5/4/14.
 */
class RideSpec
  extends Specification
  with RidePersistenceComponent
  with RideServiceComponent
  with Mockito
{

  val ridePersistence = RidePersistenceImpl
  val rideService = RideServiceImpl

  sequential


  def populateRides() = {

      io.Source.fromInputStream(
        getClass.getResourceAsStream("rides.json")).
        getLines.map(
          line => {
            Json.parse(line).validate[Ride].fold(
              valid = ride => {
                rideService.create(ride)
              },
              invalid = err => err
            )
            }
        ).
        toList
  }


  def setup() = {
    val app = new WithApplication {
      populateRides()
    }
  }

  def tearDown() = {
    val app = new WithApplication {
      rideService.deleteAll()
    }
  }

  step(setup())

  "Rides.getRideCount" should {
    "return totalRideCount if ride exists" in new WithApplication {

      rideService.getRideCount() must beEqualTo(3)

    }
  }


  "Rides.getClientCount" should {
    "return totalClientCount if ride exists" in new WithApplication {

      rideService.getClientCount() must beEqualTo(3)

    }
  }

  "Rides.getMilePerClient" should {

    "return MilesCount if exists per client" in new WithApplication {

      val milesPerClient = rideService.getMilesPerClient()

      milesPerClient.foreach {
        case (clientId:UUID, totalMiles:Double) =>
          println(s"$clientId --> travelled $totalMiles")
      }
      milesPerClient.size must beEqualTo(3)
    }
  }

  "Rides.getMedianRating" should {

    "return MedianRating if exists for a driver" in new WithApplication {

      val medianRating = rideService.getMedianRating(UUID.fromString("07a8cb65-c2e5-7537-0d1f-0606a0944b3c"))

      medianRating.get must beEqualTo(2.0)
    }
  }

  "Rides.getAvgFare" should {

    "return avg fare if exists for a city" in new WithApplication {

      val bounds = Map(
        "latMin" -> 34.123123F,
        "lonMin" -> -124.085085F,
        "latMax" -> 40.123123F,
        "lonMax" -> -119.085085F
      )
      val avgFare = rideService.getAvgFare(bounds)

      avgFare.get must beEqualTo(20.0)
    }
  }

  step(tearDown())
}

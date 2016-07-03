package com.scheduler

import java.text.SimpleDateFormat

import com.scheduler.api.SchedulerHttpService
import com.typesafe.scalalogging.LazyLogging
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class SchedulerHttpServiceSpec extends Specification with Specs2RouteTest with SchedulerHttpService with LazyLogging {
  def actorRefFactory = system
  
  "MyService" should {

    sequential
    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("<html>\n" +
          "              <body>\n       " +
          "         <h1>Welcome to\n    " +
          "              <i>My Scheduler Api</i>\n         " +
          "         !</h1>")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: POST, GET"
      }
    }

    // POST tests:

    "POST request must contain json" in {
      Post() ~> sealRoute(myRoute) ~> check {
        status.toString must_== "400 Bad Request"
        responseAs[String] must contain("Request entity expected but not supplied")
      }
    }

    val currentMiliseconds = System.currentTimeMillis()
    val sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val nowString = sdf.format(currentMiliseconds)

    "POST Json with valid fields should be accepted" in {
      Post("/", HttpEntity(MediaTypes.`application/json`,
        """{"creator_name":"foo", "event_type":"clear_cache", "event_target":"resource1", "event_time":""""+nowString+"""" }""")
      ) ~> sealRoute(myRoute) ~> check {
        status.toString must_== "201 Created"
        responseAs[String] must contain("Event Received")
      }
    }


    "POST Json must contain mandatory fields" in {
      Post("/", HttpEntity(MediaTypes.`application/json`,
        """{"creatorr_name":"foo", "event_type":"clear_cache", "event_target":"resource1", "event_time":"0-07-03 22:33:33" }""")
      ) ~> sealRoute(myRoute) ~> check {
        status.toString must_== "400 Bad Request"
        responseAs[String] must contain("Object is missing required member 'creator_name")
      }
    }

    "GET should show newly created event" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain(nowString)
      }
    }



    "POST Json must contain valid YYYY-MM-DD hh:mm:ss date" in {
      Post("/", HttpEntity(MediaTypes.`application/json`,
        """{"creator_name":"foo2", "event_type":"clear_cache", "event_target":"resource1", "event_time":"0-04-11 2:24:56" }""")
      ) ~> sealRoute(myRoute) ~> check {
        status.toString must_== "400 Bad Request"
        responseAs[String] must contain("Timestamp format must be yyyy-mm-dd hh:mm:ss")
      }
    }


  }
}

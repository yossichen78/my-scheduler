package com.scheduler

import com.scheduler.api.SchedulerHttpService
import com.scheduler.domain.ScheduleEntryJson
import com.typesafe.scalalogging.LazyLogging
import org.specs2.mutable.Specification
import spray.json.DefaultJsonProtocol
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

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
        logger.debug(responseAs[String])

        status.toString must_== "400 Bad Request"
        responseAs[String] must contain("Request entity expected but not supplied")
      }
    }


    "POST Json with valid fields should be accepted" in {
      Post("/", HttpEntity(MediaTypes.`application/json`,
        """{"creator_name":"foo", "event_type":"clear_cache", "event_target":"resource1", "event_time":"2016-04-11 2:24:56" }""")
      ) ~> sealRoute(myRoute) ~> check {
        logger.debug(responseAs[String])

        status.toString must_== "201 Created"
        responseAs[String] must contain("Event Received")
      }
    }

    "POST Json must contain mandatory fields" in {
      Post("/", HttpEntity(MediaTypes.`application/json`,
        """{"creatorr_name":"foo", "event_type":"clear_cache", "event_target":"resource1", "event_time":"2016-04-11 2:24:56" }""")
      ) ~> sealRoute(myRoute) ~> check {
        logger.debug(responseAs[String])

        status.toString must_== "400 Bad Request"
        responseAs[String] must contain("Object is missing required member 'creator_name")
      }
    }

    "POST Json must contain valid YYYY-MM-DD hh:mm:ss date" in {
      Post("/", HttpEntity(MediaTypes.`application/json`,
        """{"creator_name":"foo2", "event_type":"clear_cache", "event_target":"resource1", "event_time":"0-04-11 2:24:56" }""")
      ) ~> sealRoute(myRoute) ~> check {
        logger.debug(responseAs[String])

        status.toString must_== "400 Bad Request"
        responseAs[String] must contain("Timestamp format must be yyyy-mm-dd hh:mm:ss")
      }
    }

    // DB test:

    // todo: remove test entries from db (and use a separate DB for testing!)



  }
}

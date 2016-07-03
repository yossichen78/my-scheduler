package com.scheduler.api

import akka.actor.Actor
import com.scheduler.dao.{SelectException, InsertException, ConnectionException}
import com.scheduler.domain.{SchedulerService, ScheduleEntryJson}
import com.typesafe.scalalogging.LazyLogging
import spray.http.StatusCodes
import spray.http.MediaTypes._
import spray.routing._
import spray.json.DefaultJsonProtocol

class SchedulerHttpServiceActor extends Actor with SchedulerHttpService  {

  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

object JsonImplicits extends DefaultJsonProtocol { // required for the marshaller
  implicit val impEntry = jsonFormat4(ScheduleEntryJson)
}

 trait SchedulerHttpService extends HttpService with LazyLogging with spray.httpx.SprayJsonSupport {

  val schedulerService = new SchedulerService
  val myRoute = handleExceptions(myExceptionHandler) {
    import JsonImplicits._

    path("") {
      post { // POST events to the schedule
        decompressRequest() {
          entity(as[ScheduleEntryJson]) { entry => //todo: more detailed marshalling to get real case class
            detach() {
              schedulerService.handlePostRequest(entry) match {
                case entry: ScheduleEntryJson =>
                  complete (StatusCodes.Created, "Event Received")

              }
            }
          }
        }
      } ~
      get { //prints all events from the schedule to a table
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here

          complete {
            val entries = schedulerService.handleGetRequest

            <html>
              <body>
                <h1>Welcome to
                  <i>My Scheduler Api</i>
                  !</h1>
                <table>
                <thead>
                  <tr>
                    <td>Creator Name:</td>
                    <td>Event Type:</td>
                    <td>Event Time:</td>
                    <td>Event Target:</td>
                  </tr>
                </thead>
                  <tbody>
                    {for (entry <- entries) yield
                    <tr>
                      <td>{entry.creator_name}</td>
                      <td>{entry.event_type}</td>
                      <td>{entry.event_time}</td>
                      <td>{entry.event_target}</td>
                    </tr>
                    }
                  </tbody>
                </table>
              </body>
            </html>
          }
        }
      }
    }
  }


 implicit def myExceptionHandler: ExceptionHandler =
   ExceptionHandler {
     case exception: ConnectionException =>
       logger.error(s"ConnectionException", exception)
       complete(StatusCodes.InternalServerError, "ConnectionException: "+exception.getMessage)
     case exception: InsertException =>
       logger.error(s"InsertException", exception)
       complete(StatusCodes.InternalServerError, "InsertException: "+exception.getMessage)
     case exception: SelectException =>
       logger.error(s"SelectException", exception)
       complete(StatusCodes.InternalServerError, "SelectException: "+exception.getMessage)
     case exception: IllegalArgumentException =>
       logger.error(s"Invalid value", exception)
       complete(StatusCodes.BadRequest, "IllegalArgumentException: "+exception.getMessage)
     case e : Exception =>
       logger.error(s"Unknown Error",e)
       complete(StatusCodes.InternalServerError, "Unknown Error")
   }

}


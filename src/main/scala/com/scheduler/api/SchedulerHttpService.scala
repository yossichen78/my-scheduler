package com.scheduler.api

import akka.actor.Actor
import com.scheduler.dao.{SelectException, InsertException, ConnectionException}
import com.scheduler.domain.{SchedulerService, ScheduleEntryJson}
import com.typesafe.scalalogging.LazyLogging
import spray.http.MediaTypes._
import spray.routing._
import spray.json.DefaultJsonProtocol

class SchedulerHttpServiceActor extends Actor with SchedulerHttpService  {

  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

object JsonImplicits extends DefaultJsonProtocol {

  implicit val impEntry = jsonFormat4(ScheduleEntryJson)
}

 trait SchedulerHttpService extends HttpService with LazyLogging with spray.httpx.SprayJsonSupport {

  val schedulerService = new SchedulerService
  val myRoute = handleExceptions(myExceptionHandler) {
    import JsonImplicits._

    path("") { //sends events to the schedule
      post {
        decompressRequest() {
          entity(as[ScheduleEntryJson]) { entry =>
            detach() {
              complete {
                logger.debug("API received event: "+entry.toString)
                schedulerService.handlePostRequest(entry) match {
                  case entry: ScheduleEntryJson => "Event Received"
                }
              }
            }
          }
        }
      } ~
      get { //prints all events from the schedule to a table
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here

          complete {
            logger.debug("get request!!")
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
         complete("ConnectionException: "+exception.getMessage)
       case exception: InsertException =>
         logger.error(s"InsertException", exception)
         complete("InsertException: "+exception.getMessage)
       case exception: SelectException =>
         logger.error(s"SelectException", exception)
         complete("SelectException: "+exception.getMessage)
       case exception: IllegalArgumentException =>
         logger.error(s"Invalid value", exception)
         complete("IllegalArgumentException: "+exception.getMessage)
       case _ : Exception =>
         logger.error(s"Unknown Error")
         complete("Unknown Error")
     }

}


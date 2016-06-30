package com.scheduler.api

import akka.actor.Actor
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
  val myRoute = {
    import JsonImplicits._

    path("") { //sends events to the schedule
      post {
        decompressRequest() {
          entity(as[ScheduleEntryJson]) { entry =>
            detach() {
              complete {
                logger.debug("API received event: "+entry.toString)
                schedulerService.handlePostRequest(entry)
                "Event Received"
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
                    <td>Action Type:</td>
                    <td>Action Time:</td>
                    <td>Action Target:</td>
                  </tr>
                </thead>
                  <tbody>
                    {for (entry <- entries) yield
                    <tr>
                      <td>{entry.creator_name}</td>
                      <td>{entry.event_type}</td>
                      <td>{entry.action_time}</td>
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

}


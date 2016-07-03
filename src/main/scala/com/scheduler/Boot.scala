package com.scheduler


import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.scheduler.api.SchedulerHttpServiceActor
import com.scheduler.domain.{ScheduleEntryJson, EventReaderActor}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App with LazyLogging {

  implicit val system = ActorSystem("on-spray-can")

  // create and start the http service actor
  val service = system.actorOf(Props[SchedulerHttpServiceActor], "demo-service")

  import system.dispatcher

  // create the schedule checker actor.
  val eventReaderActor = system.actorOf(Props(classOf[EventReaderActor]))



  // call the schedule checker actor and tell it to check the schedule db once a minute.
  val cancellable =
    system.scheduler.schedule(
      0 milliseconds,
      60 seconds,
      eventReaderActor,
      "checkDB")

  implicit val timeout = Timeout(5.seconds)

  // start a new HTTP server

  val conf = ConfigFactory.load()
  val server = conf.getConfig("env")
  val host = server.getString("host")
  val port = server.getInt("port")

  IO(Http) ? Http.Bind(service, interface = host, port = port)
}

object MySchedule {
  def events = List[ScheduleEntryJson]()
}

package com.scheduler.domain

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by yossichen78 on 30/06/2016.
  */
class EventReaderActor extends Actor with LazyLogging {
  def receive = {
    case "checkDB" => logger.debug("checking db")
  }
}

class Schedule extends Actor with LazyLogging {
  var eventList = List[ScheduleEntryJson]()
  def receive = {
    case AddEvent(entry) => eventList = eventList :+ entry
    case GetNextMinuteEvents =>

  }
}

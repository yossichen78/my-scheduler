package com.scheduler.domain

import akka.actor.Actor
import com.scheduler.dao.ScheduleDao
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by yossichen78 on 30/06/2016.
  */
class EventReaderActor extends Actor with LazyLogging {
  def receive = {
    case "checkDB" =>
      logger.debug("checking db")
      val list = ScheduleDao.getNextMinuteEvents
      logger.debug("found "+list.length+" events")
      for (item <- list) yield {
        logger.debug(item.toString)
      }
  }
}


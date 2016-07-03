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
      val list = ScheduleDao.getNextMinuteEvents   // this gets all events from the following minute
      logger.info("found "+list.length+" events for the next minute")
      for (item <- list) yield {
        item.event_type match {
          case "welcome_email" => //these call to the the email service etc
            logger.info("Sending a welcome email to "+item.event_target)
          case "clear_cache" =>
            logger.info("Clearing cache from "+item.event_target)
          case "meeting_reminder" =>
            logger.info("Sending a reminder for meeting between "+item.creator_name+" and "+item.event_target)
        }
      }
  }
}


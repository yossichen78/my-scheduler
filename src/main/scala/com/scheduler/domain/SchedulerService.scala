package com.scheduler.domain

import java.sql.Timestamp

import com.scheduler.dao.ScheduleDao
import com.sun.javaws.exceptions.InvalidArgumentException
import com.typesafe.scalalogging.LazyLogging


/**
  * Created by yossichen78 on 30/06/2016.
  */
class SchedulerService extends LazyLogging {

  var eventList = List[ScheduleEntryJson]()

  def handlePostRequest (entry: ScheduleEntryJson): ScheduleEntryJson = {
    Timestamp.valueOf(entry.event_time) // this ensures event time is a valid timestamp string
    entry.event_type match { // todo: use enums
      case "welcome_email" | "clear_cache" | "meeting_reminder" =>
      case _ => throw InvalidArgumentException
    }
    entry.creator_name match {
      case "" => throw InvalidArgumentException
    }
    entry.event_target match {
      case "" => throw InvalidArgumentException
    }

    ScheduleDao.post(entry)
  }

  def handleGetRequest : List[ScheduleEntryJson] = {
    ScheduleDao.get
  }

}

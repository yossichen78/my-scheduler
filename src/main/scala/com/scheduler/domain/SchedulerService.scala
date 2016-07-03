package com.scheduler.domain

import java.sql.Timestamp

import com.scheduler.dao.ScheduleDao
import com.typesafe.scalalogging.LazyLogging


/**
  * Created by yossichen78 on 30/06/2016.
  */
class SchedulerService extends LazyLogging {

  def handlePostRequest (entry: ScheduleEntryJson): ScheduleEntryJson = {
    Timestamp.valueOf(entry.event_time) // this ensures event time is a valid timestamp string
    entry.event_type match { //
      case "welcome_email" | "clear_cache" | "meeting_reminder" =>
      case _ => throw new IllegalArgumentException("allowed values for event_type: \"welcome_email\" | \"clear_cache\" | \"meeting_reminder\"")
    }
    entry.creator_name match {
      case "" => throw new IllegalArgumentException("creator_name can't be empty")
      case _ =>
    }
    entry.event_target match {
      case "" => throw new IllegalArgumentException("event_target can't be empty")
      case _ =>
    }

    ScheduleDao.post(entry)
  }

  def handleGetRequest : List[ScheduleEntryJson] = {
    ScheduleDao.get
  }

}

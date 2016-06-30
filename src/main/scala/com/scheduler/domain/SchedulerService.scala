package com.scheduler.domain

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by yossichen78 on 30/06/2016.
  */
class SchedulerService extends LazyLogging {

  var eventList = List[ScheduleEntryJson]()

  def handlePostRequest (entry: ScheduleEntryJson) = {

    eventList = eventList :+ entry

    logger.debug(eventList.length.toString)
  }

  def handleGetRequest  = {

    eventList
  }

}

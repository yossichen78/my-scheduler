package com.scheduler.dao

import java.sql.{DriverManager, Connection}
import java.text.SimpleDateFormat
import com.scheduler.domain.ScheduleEntryJson
import com.typesafe.scalalogging.LazyLogging

import com.typesafe.config.ConfigFactory

/**
  * A Scala JDBC connection example by Alvin Alexander,
  * http://alvinalexander.com
  */
object ScheduleDao extends LazyLogging {

  val conf = ConfigFactory.load()
  val db = conf.getConfig("mysqlDB")
  val driver = db.getString("driver")
  val url = db.getString("url")
  val username = db.getString("user")
  val password = db.getString("password")

  var connection : Connection = null
  implicit val ec = scala.concurrent.ExecutionContext.global

  def connect: Connection = {
    try {
      Class.forName(driver)
      DriverManager.getConnection(url, username, password)
    } catch {
      case e : Exception =>
        logger.error("ConnectionException", e)
        throw ConnectionException("Unable to connect to DB at "+url+" with user "+username+" and password "+password)
    }
  }

  def post(entry: ScheduleEntryJson) : ScheduleEntryJson = {
    connection = connect
    try {
      // create the statement, and run the update
      val statement = connection.createStatement()
      val statementString = "INSERT INTO schedule (`creator_name`,`event_type`,`event_target`,`event_time`) " +
        "VALUES ('" + entry.creator_name + "','" + entry.event_type + "','" + entry.event_target + "','" + entry.event_time + "')"
      val resultSet = statement.executeUpdate(statementString)
      connection.close()
      if (resultSet == 1){
        entry
      } else {
        logger.error("Entry was not inserted")
        throw InsertException("failed to insert event: " + entry)
      }
    } catch {
      case e: Exception =>
        logger.error("InsertException", e)
        throw InsertException("failed to insert event: " + entry)
    }
  }

  def getNextMinuteEvents : List[ScheduleEntryJson] = {
    connection = connect
    var list = List[ScheduleEntryJson]()
    try {
      // create the statement, and run the select query
      val statement = connection.createStatement()

      val currentMiliseconds = System.currentTimeMillis()
      val roundedToMinutes = Math.floor(currentMiliseconds/(60*1000)).toLong * 60*1000

      val sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
      val nowMilis = new java.util.Date(roundedToMinutes)
      val nowPlusMinuteMilis = new java.util.Date(roundedToMinutes+60*1000-1)
      val now = sdf.format(nowMilis)
      val nowPlusMinute = sdf.format(nowPlusMinuteMilis)

      val queryString = "SELECT * FROM schedule WHERE `event_time` BETWEEN '"+now+"' AND '"+nowPlusMinute+"'"

      val resultSet = statement.executeQuery(queryString)
      while (resultSet.next()) {
        val creatorName = resultSet.getString("creator_name")
        val eventTarget = resultSet.getString("event_target")
        val eventTime = resultSet.getString("event_time")
        val eventType = resultSet.getString("event_type")
        val item = new ScheduleEntryJson(creatorName, eventType, eventTarget, eventTime)
        list = list :+ item
      }
    } catch {
      case e: Exception =>
        logger.error("SelectException", e)
        throw SelectException("failed to select events")
    }
    connection.close()
    list

  }

  def get : List[ScheduleEntryJson] = {
    connection = connect
    var list = List[ScheduleEntryJson]()
    try {
      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT * FROM schedule")
      while (resultSet.next()) {
        val creatorName = resultSet.getString("creator_name")
        val eventTarget = resultSet.getString("event_target")
        val eventTime = resultSet.getString("event_time")
        val eventType = resultSet.getString("event_type")
        val item = new ScheduleEntryJson(creatorName, eventType, eventTarget, eventTime)
        list = list :+ item
      }
    } catch {
      case e: Exception =>
        logger.error("SelectException", e)
        throw SelectException("failed to select events")
    }
    connection.close()
    list

  }

}

final case class ConnectionException(msg: String) extends Exception(msg)
final case class InsertException(msg: String) extends Exception(msg)
final case class SelectException(msg: String) extends Exception(msg)

package com.smack.actor.router.route

import akka.actor.Actor
import akka.actor.ActorLogging
import scala.collection.JavaConversions._
import com.smack.model.QueryEvent
import com.typesafe.config.Config
import com.smack.cassandra.EventService
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorRef
import com.smack.util.JsonUtil
import com.smack.cassandra.NginxLogService
import com.smack.cassandra.StatusRealStaticsService
import com.smack.cassandra.RequestRealStaticsService
import com.smack.cassandra.RequestBathStaticsService

class OutputWorker(config: Config) extends Actor with ActorLogging {

  def receive = {
    case evt: QueryEvent =>
      log.info(evt.toString())
      deal(evt, sender())
    case x => log.info("OutputWorker=" + x)
  }

  def deal(evt: QueryEvent, actor: ActorRef) {
    try {
      evt.querytype match {
        case "nginx_log" =>
          val result = EventService.getListByserverName(evt.namespace, evt.serverName, 10)
          result onComplete {
            case Success(eventList) => actor ! JsonUtil.toJson(eventList)
            case x => actor ! x
          }
        case "nginx_base_log" =>
          val result1 = NginxLogService.getListByserverName(evt.namespace, evt.serverName, 10)
          result1 onComplete {
            case Success(eventList) => actor ! JsonUtil.toJson(eventList)
            case x => actor ! x
          }
        case "status_real_statics" =>
          val result2 = StatusRealStaticsService.getListByserverName(evt.namespace, evt.serverName, 10)
          result2 onComplete {
            case Success(eventList) => actor ! JsonUtil.toJson(eventList)
            case x => actor ! x
          }
        case "request_real_statics" =>
          val result3 = RequestRealStaticsService.getListByserverName(evt.namespace, evt.serverName, 10)
          result3 onComplete {
            case Success(eventList) => actor ! JsonUtil.toJson(eventList)
            case x => actor ! x
          }
        case "request_bath_statics" =>
          val result4 = RequestBathStaticsService.getListByserverName(evt.namespace, evt.serverName, 10)
          result4 onComplete {
            case Success(eventList) => actor ! JsonUtil.toJson(eventList)
            case x => actor ! x
          }
        case _ => actor ! "not found"
      }

    } catch {
      case t: Throwable => t.fillInStackTrace()
    }

  }
}
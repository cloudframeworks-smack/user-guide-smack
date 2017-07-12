package com.smack.actor.http

import akka.actor._
import spray.routing.RequestContext
import java.util.HashMap
import java.util.ArrayList
import spray.can.Http
import spray.util._
import spray.http._
import HttpMethods._
import MediaTypes._
import akka.util.Timeout
import spray.httpx.encoding.Gzip
import java.nio.charset.Charset
import com.smack.model._
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import com.typesafe.config.Config

case class RetrievalTimeout()

class DataService(config: Config, workerRouter: ActorRef) extends Actor with HttpRouter with ActorLogging {

  import context.dispatcher
  def actorRefFactory = context
  def receive = runRoute(route)

  val rpcActor = context.system.actorSelection(config.getString("rpc.path"))

  override def doHistoryQuery(ctx: RequestContext, querytype: String, namespace: String, serviceName: String, start: Int, end: Int): Unit = {
    log.info("request uri=[{}]", ctx.request.uri)
    if (start < 0 || end < 0 || start + end <= 0) {
      ctx.responder ! entityResponse("")
    } else {
      context.actorOf {
        Props {
          new Actor with ActorLogging {
            val startTime = System.currentTimeMillis()
            def receive = {
              case history: String =>
                ctx.responder ! entityResponse(history)
                log.info("request [{}] cost time:[{}]", ctx.request.uri, System.currentTimeMillis() - startTime)
                context.stop(self)
              case x =>
                context.stop(self)
            }
            var evt = QueryEvent(querytype, namespace, serviceName, start, end)
            workerRouter tell (ConsistentHashableEnvelope(evt, evt), self)
          }
        }
      }
    }
  }

  override def doPushData(ctx: RequestContext, namespace: String, serviceName: String, msg: String) = {
    val evt = MsgEvent(namespace, serviceName, msg)
    workerRouter tell (ConsistentHashableEnvelope(evt, evt), self)
    ctx.responder ! entityResponse("""{"result":"ok"}""")
  }

  def entityResponse(json: String): HttpResponse = {
    Gzip.encode(HttpResponse(status = 200, entity = HttpEntity(`application/json`, json)))
  }

  override def doCmd(ctx: RequestContext, namespace: String, serviceName: String, cmd: String, start_time: String, end_time: String) = {
    log.info("request uri=[{}]", ctx.request.uri)
    context.actorOf {
      Props {
        new Actor with ActorLogging {
          val startTime = System.currentTimeMillis()
          def receive = {
            case history: String =>
              log.info("history===" + history)
              ctx.responder ! entityResponse(history)
              log.info("request [{}] cost time:[{}]", ctx.request.uri, System.currentTimeMillis() - startTime)
              context.stop(self)
            case x =>
              context.stop(self)
          }

          val evt = CmdMessage(namespace, serviceName, cmd, start_time, end_time)
          rpcActor ! evt
        }
      }
    }
  }

  override def doCmdStatics(ctx: RequestContext, namespace: String, serviceName: String, cmd: String, start_time: String, end_time: String, time_type: String) = {
    log.info("request uri=[{}]", ctx.request.uri)
    context.actorOf {
      Props {
        new Actor with ActorLogging {
          val startTime = System.currentTimeMillis()
          def receive = {
            case history: String =>
              log.info("history===" + history)
              ctx.responder ! entityResponse(history)
              log.info("request [{}] cost time:[{}]", ctx.request.uri, System.currentTimeMillis() - startTime)
              context.stop(self)
            case x =>
              context.stop(self)
          }

          val evt = CmdMessageByPeriod(namespace, serviceName, cmd, start_time, end_time, time_type)
          rpcActor ! evt
        }
      }
    }
  }
}
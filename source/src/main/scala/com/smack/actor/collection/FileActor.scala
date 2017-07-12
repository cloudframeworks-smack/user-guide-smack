package com.smack.actor.collection

import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSelection
import scala.collection.JavaConversions._
import scala.io.Source
import com.smack.model.MsgEvent
import com.typesafe.config.Config
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope

class FileActor(config: Config, workerRouter: ActorRef) extends Actor with ActorLogging {
	import context.dispatcher

	context.system.scheduler.scheduleOnce(30.seconds, self, "Load")

	def receive = {
		case "Load" =>
			for (line <- Source.fromFile(config.getString("log.file_path")).getLines) {
				val evt = MsgEvent("test", "test", line)
				//log.info(evt.toString())
				workerRouter tell (ConsistentHashableEnvelope(evt, evt), self)
				Thread.sleep(config.getInt("log.sleeptime"))
			}
		case x => log.info("xxxx" + x)
	}
}
package com.smack.actor.collection

import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.zeromq.ZMQMessage
import akka.zeromq.ZeroMQExtension
import akka.zeromq.SocketType
import akka.zeromq.Connect
import akka.zeromq.Listener
import akka.zeromq.Subscribe
import akka.actor.ActorSelection
import scala.collection.JavaConversions._
import com.smack.model.MsgEvent
import com.typesafe.config.Config
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope

class ZeroMqActor(config: Config, workerRouter: ActorRef) extends Actor with ActorLogging {
	import context.dispatcher
	var subActor: ActorRef = null

	//context.system.scheduler.schedule(10.seconds, 10.seconds, self, "RePly")

	var time = 0
	var lastTime = System.currentTimeMillis()
	override def preStart(): Unit = {
		if (subActor == null) {
			val topic=config.getString("zeromq.topic")
			val address=config.getString("zeromq.address")
			subActor = ZeroMQExtension(context.system).newSocket(SocketType.Sub, Connect(address), Listener(self), Subscribe(topic))
			log.info("[{}] 成功订阅topic [{}]", topic, address)
		}
	}

	def receive = {
		case "RePly" =>
			log.info("===")
		case m: ZMQMessage =>
			try {
				val evt = MsgEvent(new String(m.frames(0).utf8String), new String(m.frames(1).utf8String), new String(m.frames(1).utf8String))
				workerRouter tell (ConsistentHashableEnvelope(evt, evt), self)
			} catch {
				case t: Throwable => t.printStackTrace()
			}
		case x => log.info("xxxxxxxxxxxxx" + x)
	}
}
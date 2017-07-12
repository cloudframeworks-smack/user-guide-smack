package com.smack.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.DeadLetter

class DeadLetterListener extends Actor with ActorLogging {
	def receive = {
		case d: DeadLetter => log.info(d.sender.toString)
		case x => log.info("x")
	}
}
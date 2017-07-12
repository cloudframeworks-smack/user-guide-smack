package com.smack

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.Logging
import com.smack.actor.DeadLetterListener
import akka.actor.DeadLetter

import com.smack.actor.DataOutputActor

object DataOutputServer extends App {
	var port = "2551"
	if (!args.isEmpty) port=args(0)
	System.setProperty("akka.remote.netty.tcp.port", port)
	val config = ConfigFactory.load("data-output.conf")
	implicit val system = ActorSystem("DataOutputServer", config)
	val log = Logging(system, "")
	
	//define dead letter deal strategy
	val listener = system.actorOf(Props[DeadLetterListener], "deadLetter")
    system.eventStream.subscribe(listener, classOf[DeadLetter])
    
    //define main actor
    system.actorOf(Props(new DataOutputActor(config)), "dataoutput")
    
	log.info("Starting Data Input Server Ok!")
}
package com.smack.spark.rpc

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.Logging
import com.smack.spark.rpc.actor.RpcActor

import com.smack.model.CmdMessage
import com.smack.model.CmdMessageByPeriod

object SparkRpcForActor extends App {
	SparkConfig
	var host = "0.0.0.0"
	if (!args.isEmpty) {
		host = args(0)
	} else {
		System.out.println("need start parameter !")
		System.exit(0)
	}
	System.setProperty("akka.remote.netty.tcp.hostname", host)
	System.setProperty("akka.remote.netty.tcp.port", "12551")
	val config = ConfigFactory.load("akka.conf")
	implicit val system = ActorSystem("RpcForActor", config)
	val actor = system.actorOf(Props[RpcActor], "rpcActor")
	System.out.println(actor)
	System.out.println("Starting Spark Rpc func !")
}
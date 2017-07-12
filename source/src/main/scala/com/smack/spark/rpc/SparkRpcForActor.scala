package com.smack.spark.rpc

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.Logging
import com.smack.spark.rpc.actor.RpcActor


import com.smack.model.CmdMessage
import com.smack.model.CmdMessageByPeriod

object SparkRpcForActor extends App {
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
  
  //actor ! CmdMessage("wanda-sit","xapi-activity-d","uv","2017-06-21 01:00:18","2017-06-21 01:25:10")
  //actor ! CmdMessageByPeriod("test","test","uv","2017-06-01 01:00:18","2017-06-28 01:25:10","minute")
  System.out.println("Starting Spark Rpc func !")
}
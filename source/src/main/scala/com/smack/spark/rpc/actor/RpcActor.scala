package com.smack.spark.rpc.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import java.util.HashMap
import com.smack.util.JsonUtil
import com.smack.model.CmdMessage
import com.smack.model.CmdMessageByPeriod
import com.smack.spark.rpc.service.RankDataService

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Cluster.Builder

class RpcActor extends Actor with ActorLogging {
	val rankDataDealService = new RankDataService()
	def receive = {
		case cmdMsgeGroup: CmdMessageByPeriod => {
			cmdMsgeGroup.cmd match {
				case "avgtime" =>
					sender ! rankDataDealService.handlerAvgTime(cmdMsgeGroup)
				case "pv" =>
					sender ! rankDataDealService.handlerPv(cmdMsgeGroup)
				case "uv" =>
					sender ! rankDataDealService.handlerUv(cmdMsgeGroup)
				case _ =>
					sender ! JsonUtil.toJson(Map("message" -> "not found cmd"))
			}
		}
		case x => log.info("xxxx" + x)
	}
}
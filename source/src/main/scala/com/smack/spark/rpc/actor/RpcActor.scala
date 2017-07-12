package com.smack.spark.rpc.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import java.util.HashMap
import com.smack.util.JsonUtil
import com.smack.model.CmdMessage
import com.smack.model.CmdMessageByPeriod
import com.smack.spark.rpc.service.DataService
import com.smack.spark.rpc.service.RankDataService

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Cluster.Builder

class RpcActor extends Actor with ActorLogging {
	val dataDealService = new DataService()
	val rankDataDealService = new RankDataService()
	def receive = {
		case cmdMsg: CmdMessage => {
			cmdMsg.cmd match {
				case "avgtime" =>
					sender ! dataDealService.handlerAvgTime(cmdMsg)
				case "pv" =>
					sender ! dataDealService.handlerPv(cmdMsg)
				case "uv" =>
					sender ! dataDealService.handlerUv(cmdMsg)
				case _ =>
					sender ! JsonUtil.toJson(Map("message" -> "not found cmd"))
			}
		}
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
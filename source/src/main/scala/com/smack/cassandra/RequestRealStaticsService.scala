package com.smack.cassandra

import com.outworkers.phantom.dsl._

import scala.concurrent.Future

trait RequestRealStaticsService extends ProductionDatabase {

	def getListByserverName(namespace: String, servername: String, num: Integer): Future[List[RequestRealStatics]] = {
		database.requestRealStaticsModel.getListByCondition(namespace, servername, num)
	}
}

object RequestRealStaticsService extends RequestRealStaticsService with ProductionDatabase

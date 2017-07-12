package com.smack.cassandra

import com.outworkers.phantom.dsl._

import scala.concurrent.Future

trait NginxLogService extends ProductionDatabase {
  
	def getListByserverName(namespace: String, servername: String, num: Integer): Future[List[NginxLog]] = {
		database.nginxLogModel.getListByCondition(namespace, servername, num)
	}
}

object NginxLogService extends NginxLogService with ProductionDatabase

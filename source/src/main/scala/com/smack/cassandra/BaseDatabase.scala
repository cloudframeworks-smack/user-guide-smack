package com.smack.cassandra

import com.smack.cassandra.Connector._
import com.outworkers.phantom.dsl._

class BaseDatabase(override val connector: KeySpaceDef) extends Database[BaseDatabase](connector) {

	object eventModel extends ConcreteEventModel with connector.Connector
	
	object nginxLogModel extends ConcreteNginxLogModel with connector.Connector
	
	object statusRealStaticsModel extends ConcreteStatusRealStaticsModel with connector.Connector
	
	object requestRealStaticsModel extends ConcreteRequestRealStaticsModel with connector.Connector
	
	object requestBathStaticsModel extends ConcreteRequestBathStaticsModel with connector.Connector

}

object ProductionDb extends BaseDatabase(connector)

trait ProductionDatabaseProvider {
	def database: BaseDatabase
}

trait ProductionDatabase extends ProductionDatabaseProvider {
	override val database = ProductionDb
}
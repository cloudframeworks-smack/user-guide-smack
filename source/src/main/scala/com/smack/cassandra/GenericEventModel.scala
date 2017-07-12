package com.smack.cassandra

import java.util.UUID
import com.outworkers.phantom.dsl._
import scala.concurrent.Future

class EventModel extends CassandraTable[ConcreteEventModel, Event] {

	override def tableName: String = "nginx_log"

	object namespace extends StringColumn(this) with PartitionKey

	object servername extends StringColumn(this) with PartitionKey

	object log extends StringColumn(this)
	
	//object date extends DateColumn(this)
	
	override def fromRow(r: Row): Event = Event(namespace(r), servername(r), log(r))
}

abstract class ConcreteEventModel extends EventModel with RootConnector {
	
	def getListByCondition(namespace: String, servername: String, num: Integer): Future[List[Event]] = {
		select
			.where(_.namespace eqs namespace).and { _.servername eqs servername}
			.consistencyLevel_=(ConsistencyLevel.ONE).allowFiltering()
			.limit(num).fetch()
	}
}

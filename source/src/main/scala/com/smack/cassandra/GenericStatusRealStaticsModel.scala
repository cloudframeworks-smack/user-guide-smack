package com.smack.cassandra

import java.util.UUID
import com.outworkers.phantom.dsl._
import scala.concurrent.Future

class StatusRealStaticsModel extends CassandraTable[ConcreteStatusRealStaticsModel, StatusRealStatics] {

	override def tableName: String = "status_real_statics"

	object namespace extends StringColumn(this) with PartitionKey

	object servername extends StringColumn(this) with PartitionKey

	object status extends IntColumn(this)

	object start_time extends StringColumn(this)

	object end_time extends StringColumn(this)

	object num extends IntColumn(this)

	override def fromRow(r: Row): StatusRealStatics = StatusRealStatics(namespace(r), servername(r),
		status(r), start_time(r), end_time(r), num(r))
}

abstract class ConcreteStatusRealStaticsModel extends StatusRealStaticsModel with RootConnector {

	def getListByCondition(namespace: String, servername: String, num: Integer): Future[List[StatusRealStatics]] = {
		select
			.where(_.namespace eqs namespace).and { _.servername eqs servername }
			.consistencyLevel_=(ConsistencyLevel.ONE).allowFiltering()
			.limit(num).fetch()
	}
}

package com.smack.cassandra

import java.util.UUID
import com.outworkers.phantom.dsl._
import scala.concurrent.Future

class RequestRealStaticsModel extends CassandraTable[ConcreteRequestRealStaticsModel, RequestRealStatics] {

	override def tableName: String = "request_real_statics"

	object namespace extends StringColumn(this) with PartitionKey

	object servername extends StringColumn(this) with PartitionKey

	object request extends StringColumn(this)

	object start_time extends StringColumn(this)

	object end_time extends StringColumn(this)

	object num extends IntColumn(this)
	
	object avg_time extends DoubleColumn(this)
	

	override def fromRow(r: Row): RequestRealStatics = RequestRealStatics(namespace(r), servername(r),
		request(r), start_time(r), end_time(r), num(r), avg_time(r))
}

abstract class ConcreteRequestRealStaticsModel extends RequestRealStaticsModel with RootConnector {

	def getListByCondition(namespace: String, servername: String, num: Integer): Future[List[RequestRealStatics]] = {
		select
			.where(_.namespace eqs namespace).and { _.servername eqs servername }
			.consistencyLevel_=(ConsistencyLevel.ONE).allowFiltering()
			.limit(num).fetch()
	}
}

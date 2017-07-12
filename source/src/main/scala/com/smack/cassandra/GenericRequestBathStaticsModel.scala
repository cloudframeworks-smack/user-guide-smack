package com.smack.cassandra

import java.util.UUID
import com.outworkers.phantom.dsl._
import scala.concurrent.Future

class RequestBathStaticsModel extends CassandraTable[ConcreteRequestBathStaticsModel, RequestBathStatics] {

	override def tableName: String = "request_bath_statics"

	object namespace extends StringColumn(this) with PartitionKey

	object servername extends StringColumn(this) with PartitionKey

	object request extends StringColumn(this)

	object num extends IntColumn(this)

	override def fromRow(r: Row): RequestBathStatics = RequestBathStatics(namespace(r), servername(r),
		request(r), num(r))
}

abstract class ConcreteRequestBathStaticsModel extends RequestBathStaticsModel with RootConnector {


	def getListByCondition(namespace: String, servername: String, num: Integer): Future[List[RequestBathStatics]] = {
		select
			.where(_.namespace eqs namespace).and { _.servername eqs servername }
			.consistencyLevel_=(ConsistencyLevel.ONE).allowFiltering()
			.limit(num).fetch()
	}

}

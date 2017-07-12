package com.smack.cassandra

import java.util.UUID
import com.outworkers.phantom.dsl._
import scala.concurrent.Future

class NginxLogModel extends CassandraTable[ConcreteNginxLogModel, NginxLog] {

	override def tableName: String = "nginx_base_log"
	
	object namespace extends StringColumn(this) with PartitionKey

	object servername extends StringColumn(this) with PartitionKey

	object remoteaddr extends StringColumn(this)

	object remoteuser extends StringColumn(this)

	object timelocal extends StringColumn(this)

	object request extends StringColumn(this)

	object status extends IntColumn(this)

	object bodysize extends IntColumn(this)

	object httpreferer extends StringColumn(this)

	object httpuseragent extends StringColumn(this)

	object httpxforwardedfor extends StringColumn(this)

	object requestlength extends IntColumn(this)

	object upstreamresponsetime extends DoubleColumn(this)

	object upstreamaddr extends StringColumn(this)

	override def fromRow(r: Row): NginxLog = NginxLog(namespace(r), servername(r),
		remoteaddr(r), remoteuser(r), timelocal(r), request(r), status(r), bodysize(r), httpreferer(r),
		httpuseragent(r), httpxforwardedfor(r), requestlength(r), upstreamresponsetime(r), upstreamaddr(r))
}

abstract class ConcreteNginxLogModel extends NginxLogModel with RootConnector {

	def getListByCondition(namespace: String, servername: String, num: Integer): Future[List[NginxLog]] = {
		select
			.where(_.namespace eqs namespace).and { _.servername eqs servername }
			.consistencyLevel_=(ConsistencyLevel.ONE).allowFiltering()
			.limit(num).fetch()
	}
}

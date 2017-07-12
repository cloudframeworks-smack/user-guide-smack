package com.smack.flink.stream

import java.util.concurrent.TimeUnit

import java.util.UUID
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction
import org.influxdb.{ InfluxDB, InfluxDBFactory }
import org.influxdb.dto.Point

class BaseLogInfluxDBSink(measurement: String) extends RichSinkFunction[AccessBaseLog] {

	private val dataBaseName = "smack"
	var influxDB: InfluxDB = null

	override def open(parameters: Configuration): Unit = {
		super.open(parameters)
		influxDB = InfluxDBFactory.connect("http://influxdb:8086", "admin", "admin")
		influxDB.createDatabase(dataBaseName)
		influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS)
	}

	override def close(): Unit = {
		super.close()
	}

	override def invoke(in: AccessBaseLog): Unit = {
		val builder = Point.measurement(measurement)
			.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
			.tag("id", UUID.randomUUID().toString())
			.addField("namespace", in.namespace)
			.addField("servername", in.serverName)
			.addField("log", in.log)

		val p = builder.build()

		influxDB.write(dataBaseName, "autogen", p)
	}
}

class RealStatusInfluxDBSink(measurement: String) extends RichSinkFunction[AccessStatusLog] {

	private val dataBaseName = "smack"
	var influxDB: InfluxDB = null

	override def open(parameters: Configuration): Unit = {
		super.open(parameters)
		influxDB = InfluxDBFactory.connect("http://influxdb:8086", "admin", "admin")
		influxDB.createDatabase(dataBaseName)
		influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS)
	}

	override def close(): Unit = {
		super.close()
	}

	override def invoke(in: AccessStatusLog): Unit = {
		val builder = Point.measurement(measurement)
			.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
			.tag("id", UUID.randomUUID().toString())
			.addField("namespace", in.namespace)
			.addField("servername", in.serverName)
			.addField("status", in.status)
			.addField("start_time", in.start_time)
			.addField("end_time", in.end_time)
			.addField("num", in.num)

		val p = builder.build()

		influxDB.write(dataBaseName, "autogen", p)
	}
}

class RealRequestInfluxDBSink(measurement: String) extends RichSinkFunction[AccessRequestLog] {

	private val dataBaseName = "smack"
	var influxDB: InfluxDB = null

	override def open(parameters: Configuration): Unit = {
		super.open(parameters)
		influxDB = InfluxDBFactory.connect("http://influxdb:8086", "admin", "admin")
		influxDB.createDatabase(dataBaseName)
		influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS)
	}

	override def close(): Unit = {
		super.close()
	}

	override def invoke(in: AccessRequestLog): Unit = {
		val builder = Point.measurement(measurement)
			.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
			.tag("id", UUID.randomUUID().toString())
			.addField("namespace", in.namespace)
			.addField("servername", in.serverName)
			.addField("request", in.request)
			.addField("start_time", in.start_time)
			.addField("end_time", in.end_time)
			.addField("num", in.num)
			.addField("avg_time", in.avg_time)

		val p = builder.build()

		influxDB.write(dataBaseName, "autogen", p)
	}
}
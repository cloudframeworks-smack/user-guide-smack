package com.smack.flink.stream

import java.text.SimpleDateFormat
import java.util.Properties
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09
import org.apache.flink.streaming.util.serialization.SimpleStringSchema
import org.apache.flink.api.common.functions.FilterFunction
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector
import org.apache.flink.api.java.tuple.Tuple16
import org.apache.flink.api.java.tuple.Tuple7
import org.apache.flink.api.java.tuple.Tuple8
import org.apache.flink.api.java.tuple.Tuple4
import java.util.Locale;

import java.util.UUID
import java.util.Date
import org.apache.flink.streaming.connectors.cassandra.CassandraTupleSink
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder
import com.datastax.driver.core.Cluster
import com.smack.util.CassandraUtil

object KafkaNewConsumerForActor extends App {
	CassandraUtil.initTable()

	//start stream dealing
	val topics = "test"
	val brokers = "kafka:9092";
	val cassandraHost = "cassandra"
	val group = "con-consumer-group"
	val sec = "::"
	val FIELD_SERVERNAME = 2;
	val FIELD_STATUS = 7;

	val env = StreamExecutionEnvironment.getExecutionEnvironment
	env.enableCheckpointing(5000)
	val properties = new Properties()
	properties.setProperty("bootstrap.servers", brokers)
	properties.setProperty("group.id", group)

	val stream = env.addSource(new FlinkKafkaConsumer09(topics, new SimpleStringSchema(), properties))
	val s: DataStream[String] = stream.filter(new FilterFunction[String] {
		def filter(value: String): Boolean = {
			AccessLog.isValidateLogLine(value)
		}
	})

	//cassandra
	s.map(new MapFunction[String, Tuple4[String, String, String, Date]] {
		def map(value: String) = {
			val tmp = value.split(" ")
			val log = value.substring((tmp(0) + " " + tmp(1) + " ").length())
			//val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			new Tuple4(tmp(0), tmp(1), log, new Date())
		}
	}).addSink(new CassandraTupleSink[Tuple4[String, String, String, Date]](
		"INSERT INTO mydb.nginx_log"
			+ " (namespace, servername, log, create_time)"
			+ " VALUES (?, ?, ?, ?);",
		new ClusterBuilder() {
			def buildCluster(builder: Cluster.Builder): Cluster = {
				builder.addContactPoint("cassandra").withPort(9042).build()
			}
		}));

	//influxdb
	s.map(new MapFunction[String, AccessBaseLog] {
		def map(value: String) = {
			val tmp = value.split(" ")
			val log = value.substring((tmp(0) + " " + tmp(1) + " ").length())
			new AccessBaseLog(tmp(0), tmp(1), log)
		}
	}).addSink(new BaseLogInfluxDBSink("nginx_log"))

	val s0: DataStream[AccessLog] = s.map(new MapFunction[String, AccessLog] {
		def map(value: String): AccessLog = {
			AccessLog.parseLogLine(value)
		}
	})
	val cassdata = s0.map(new MapFunction[AccessLog, Tuple16[String, String, String, String, String, String, Int, Int, String, String, String, Int, Double, String, Date, Date]] {
		def map(log: AccessLog) = {
			val sdf = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z", Locale.ENGLISH);
			val create_time = sdf.parse(log.timeLocal)
			new Tuple16(log.namespace, log.serverName, log.remoteAddr, log.remoteUser, log.timeLocal, log.request, log.status.toInt, log.bodySize.toInt, log.httpReferer, log.httpUserAgent, log.httpXForwardedFor, log.requestLength.toInt, log.upstreamResponseTime.toDouble, log.upstreamAddr, create_time, new Date())
		}
	}).addSink(new CassandraTupleSink[Tuple16[String, String, String, String, String, String, Int, Int, String, String, String, Int, Double, String, Date, Date]](
		"INSERT INTO mydb.nginx_base_log"
			+ " (namespace, servername, remoteAddr, remoteUser, timeLocal, request, status, bodySize, httpReferer, httpUserAgent, httpXForwardedFor, requestLength, upstreamResponseTime, upstreamAddr, create_time,update_time)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
		new ClusterBuilder() {
			def buildCluster(builder: Cluster.Builder): Cluster = {
				builder.addContactPoint("cassandra").withPort(9042).build()
			}
		}));

	//statics status data
	val s1 = s0.map(new MapFunction[AccessLog, (String, Int)] {
		def map(value: AccessLog) = {
			(value.namespace + "::" + value.serverName + "::" + value.status, 1)
		}
	}).keyBy(_._1).timeWindow(Time.seconds(15)).apply { (k: String, w: TimeWindow, T: Iterable[(String, Int)], out: Collector[(String, String, String, Int)]) =>
		var sum: Int = 0
		for (elem <- T) {
			sum = sum + elem._2
		}
		val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
		out.collect((k, format.format(w.getStart), format.format(w.getEnd), sum))
	}
	s1.map(new MapFunction[(String, String, String, Int), Tuple7[String, String, Int, String, String, Int, Date]] {
		def map(log: (String, String, String, Int)) = {
			val logs = log._1.split("::")
			new Tuple7(logs(0), logs(1), logs(2).toInt, log._2, log._3, log._4, new Date())
		}
	}).addSink(new CassandraTupleSink[Tuple7[String, String, Int, String, String, Int, Date]](
		"INSERT INTO mydb.status_real_statics"
			+ " (namespace, servername, status,start_time,end_time,num,create_time)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?);",
		new ClusterBuilder() {
			def buildCluster(builder: Cluster.Builder): Cluster = {
				builder.addContactPoint("cassandra").withPort(9042).build()
			}
		}));

	s1.map(new MapFunction[(String, String, String, Int), AccessStatusLog] {
		def map(log: (String, String, String, Int)) = {
			val logs = log._1.split("::")
			new AccessStatusLog(logs(0), logs(1), logs(2).toInt, log._2, log._3, log._4)
		}
	}).addSink(new RealStatusInfluxDBSink("status_real_statics"))

	//statics request response data
	val s2 = s0.map(new MapFunction[AccessLog, (String, Int, Double)] {
		def map(value: AccessLog) = {
			(value.namespace + "::" + value.serverName + "::" + value.request, 1, value.upstreamResponseTime)
		}
	}).keyBy(_._1).timeWindow(Time.seconds(15)).apply { (k: String, w: TimeWindow, T: Iterable[(String, Int, Double)], out: Collector[(String, String, String, Int, Double)]) =>
		var sum: Int = 0
		var avg: Double = 0.0
		for (elem <- T) {
			sum = sum + elem._2
			avg = avg + elem._3
		}
		val avgt = avg / sum
		val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
		out.collect((k, format.format(w.getStart), format.format(w.getEnd), sum, avgt))
	}

	s2.map(new MapFunction[(String, String, String, Int, Double), Tuple8[String, String, String, String, String, Int, Double, Date]] {
		def map(log: (String, String, String, Int, Double)) = {
			val logs = log._1.split("::")
			new Tuple8(logs(0), logs(1), logs(2), log._2, log._3, log._4, log._5, new Date())
		}
	}).addSink(new CassandraTupleSink[Tuple8[String, String, String, String, String, Int, Double, Date]](
		"INSERT INTO mydb.request_real_statics"
			+ " (namespace, servername, request,start_time,end_time,num,avg_time,create_time)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?,?);",
		new ClusterBuilder() {
			def buildCluster(builder: Cluster.Builder): Cluster = {
				builder.addContactPoint("cassandra").withPort(9042).build()
			}
		}));

	s2.map(new MapFunction[(String, String, String, Int, Double), AccessRequestLog] {
		def map(log: (String, String, String, Int, Double)) = {
			val logs = log._1.split("::")
			new AccessRequestLog(logs(0), logs(1), logs(2), log._2, log._3, log._4, log._5)
		}
	}).addSink(new RealRequestInfluxDBSink("request_real_statics"))

	env.execute()
}
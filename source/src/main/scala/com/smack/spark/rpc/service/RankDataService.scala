package com.smack.spark.rpc.service

import scala.collection.JavaConversions._
import org.apache.spark.SparkConf
import com.datastax.spark.connector.{ SomeColumns, _ }
import org.apache.spark.{ SparkConf, SparkContext }
import com.smack.spark.rpc.SparkConfig
import com.smack.spark.AccessLog
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.Encoder
import org.apache.spark.sql.SparkSession
import com.smack.util.JsonUtil
import com.smack.model.CmdMessage
import com.smack.model.CmdMessageByPeriod

import java.text.SimpleDateFormat
import scala.collection.mutable.HashMap
import org.apache.spark.sql.{ DataFrame, Row }

class RankDataService {
	
	def handlerAvgTime(cmdMsg: CmdMessageByPeriod): String = {
		val spark = SparkSession.builder().appName("Spark SQL").getOrCreate()
		import com.datastax.spark.connector._
		import spark.implicits._
		val condition = "namespace='" + cmdMsg.namespace + "' and servername='" + cmdMsg.serverName + "' and create_time>'" + cmdMsg.start_time + "' and create_time<='" + cmdMsg.end_time + "'"
		val rdd = SparkConfig.sc.cassandraTable("mydb", "nginx_base_log").select("namespace", "servername", "remoteaddr", "request", "status", "upstreamresponsetime", "create_time").where(condition)

		cmdMsg.format match {
			case "day" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()
				rddlogs.createOrReplaceTempView("nginx_base_log")
				val datadf = spark.sql("select count(1) as totalnum , sum(upstreamresponsetime) as totaltime, max(upstreamresponsetime) as maxtime, min(upstreamresponsetime) as mintime, create_time from nginx_base_log group by create_time")
				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("totalnum", "totaltime", "maxtime", "mintime", "create_time"))).collect()
				var map = new HashMap[String, HashMap[String, Any]]
				result.foreach(tp => {
					var tmap = new HashMap[String, Any]
					tmap.put("totalnum", tp.get("totalnum").get.asInstanceOf[Long])
					tmap.put("totaltime", tp.get("totaltime").get.asInstanceOf[Double])
					tmap.put("maxtime", tp.get("maxtime").get.asInstanceOf[Double])
					tmap.put("mintime", tp.get("mintime").get.asInstanceOf[Double])
					map.put(tp.get("create_time").get.asInstanceOf[String], tmap)
				})
				JsonUtil.toJson(map)
			case "hour" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd HH");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()
				rddlogs.createOrReplaceTempView("nginx_base_log")
				val datadf = spark.sql("select count(1) as totalnum , sum(upstreamresponsetime) as totaltime, max(upstreamresponsetime) as maxtime, min(upstreamresponsetime) as mintime, create_time from nginx_base_log group by create_time")
				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("totalnum", "totaltime", "maxtime", "mintime", "create_time"))).collect()
				var map = new HashMap[String, HashMap[String, Any]]
				result.foreach(tp => {
					var tmap = new HashMap[String, Any]
					tmap.put("totalnum", tp.get("totalnum").get.asInstanceOf[Long])
					tmap.put("totaltime", tp.get("totaltime").get.asInstanceOf[Double])
					tmap.put("maxtime", tp.get("maxtime").get.asInstanceOf[Double])
					tmap.put("mintime", tp.get("mintime").get.asInstanceOf[Double])
					map.put(tp.get("create_time").get.asInstanceOf[String], tmap)
				})
				JsonUtil.toJson(map)
			case "minute" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()
				rddlogs.createOrReplaceTempView("nginx_base_log")
				val datadf = spark.sql("select count(1) as totalnum , sum(upstreamresponsetime) as totaltime, max(upstreamresponsetime) as maxtime, min(upstreamresponsetime) as mintime, create_time from nginx_base_log group by create_time")
				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("totalnum", "totaltime", "maxtime", "mintime", "create_time"))).collect()
				var map = new HashMap[String, HashMap[String, Any]]
				result.foreach(tp => {
					var tmap = new HashMap[String, Any]
					tmap.put("totalnum", tp.get("totalnum").get.asInstanceOf[Long])
					tmap.put("totaltime", tp.get("totaltime").get.asInstanceOf[Double])
					tmap.put("maxtime", tp.get("maxtime").get.asInstanceOf[Double])
					tmap.put("mintime", tp.get("mintime").get.asInstanceOf[Double])
					map.put(tp.get("create_time").get.asInstanceOf[String], tmap)
				})
				JsonUtil.toJson(map)
			case _ =>
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), "")).toDF()
				rddlogs.createOrReplaceTempView("nginx_base_log")
				val datadf = spark.sql("select count(1) as totalnum , sum(upstreamresponsetime) as totaltime, max(upstreamresponsetime) as maxtime, min(upstreamresponsetime) as mintime from nginx_base_log")
				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("totalnum", "totaltime", "maxtime", "mintime"))).collect()
				var map = new HashMap[String, HashMap[String, Any]]
				result.foreach(tp => {
					var tmap = new HashMap[String, Any]
					tmap.put("totalnum", tp.get("totalnum").get.asInstanceOf[Long])
					tmap.put("totaltime", tp.get("totaltime").get.asInstanceOf[Double])
					tmap.put("maxtime", tp.get("maxtime").get.asInstanceOf[Double])
					tmap.put("mintime", tp.get("mintime").get.asInstanceOf[Double])
					map.put(tp.get("data").get.asInstanceOf[String], tmap)
				})
				JsonUtil.toJson(map)
		}
	}

	def handlerPv(cmdMsg: CmdMessageByPeriod): String = {
		val spark = SparkSession.builder().appName("Spark SQL").getOrCreate()
		import com.datastax.spark.connector._
		import spark.implicits._
		val condition = "namespace='" + cmdMsg.namespace + "' and servername='" + cmdMsg.serverName + "' and create_time>'" + cmdMsg.start_time + "' and create_time<='" + cmdMsg.end_time + "'"
		val rdd = SparkConfig.sc.cassandraTable("mydb", "nginx_base_log").select("namespace", "servername", "remoteaddr", "request", "status", "upstreamresponsetime", "create_time").where(condition)
		cmdMsg.format match {
			case "day" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()

				rddlogs.createOrReplaceTempView("nginx_base_log")

				val datadf = spark.sql("select count(1) as totalnum, create_time  from nginx_base_log group by create_time")

				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("totalnum", "create_time"))).collect()
				var map = new HashMap[String, HashMap[String, Any]]
				result.foreach(tp => {
					var tmap = new HashMap[String, Any]
					tmap.put("totalnum", tp.get("totalnum").get.asInstanceOf[Long])
					map.put(tp.get("create_time").get.asInstanceOf[String], tmap)
				})
				JsonUtil.toJson(map)
			case "hour" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd HH");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()

				rddlogs.createOrReplaceTempView("nginx_base_log")

				val datadf = spark.sql("select count(1) as totalnum, create_time  from nginx_base_log group by create_time")

				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("totalnum", "create_time"))).collect()
				var map = new HashMap[String, HashMap[String, Any]]
				result.foreach(tp => {
					var tmap = new HashMap[String, Any]
					tmap.put("totalnum", tp.get("totalnum").get.asInstanceOf[Long])
					map.put(tp.get("create_time").get.asInstanceOf[String], tmap)
				})
				JsonUtil.toJson(map)
			case "minute" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()

				rddlogs.createOrReplaceTempView("nginx_base_log")

				val datadf = spark.sql("select count(1) as totalnum, create_time  from nginx_base_log group by create_time")

				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("totalnum", "create_time"))).collect()
				var map = new HashMap[String, HashMap[String, Any]]
				result.foreach(tp => {
					var tmap = new HashMap[String, Any]
					tmap.put("totalnum", tp.get("totalnum").get.asInstanceOf[Long])
					map.put(tp.get("create_time").get.asInstanceOf[String], tmap)
				})
				JsonUtil.toJson(map)
			case _ =>
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), "")).toDF()

				rddlogs.createOrReplaceTempView("nginx_base_log")

				val datadf = spark.sql("select count(1) as totalnum  from nginx_base_log group by create_time")

				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("totalnum"))).collect()
				var map = new HashMap[String, HashMap[String, Any]]
				result.foreach(tp => {
					var tmap = new HashMap[String, Any]
					tmap.put("totalnum", tp.get("totalnum").get.asInstanceOf[Long])
					map.put(tp.get("data").get.asInstanceOf[String], tmap)
				})
				JsonUtil.toJson(map)
		}
	}

	def handlerUv(cmdMsg: CmdMessageByPeriod): String = {
		val spark = SparkSession.builder().appName("Spark SQL").getOrCreate()
		import com.datastax.spark.connector._
		import spark.implicits._
		val condition = "namespace='" + cmdMsg.namespace + "' and servername='" + cmdMsg.serverName + "' and create_time>'" + cmdMsg.start_time + "' and create_time<='" + cmdMsg.end_time + "'"
		val rdd = SparkConfig.sc.cassandraTable("mydb", "nginx_base_log").select("namespace", "servername", "remoteaddr", "request", "status", "upstreamresponsetime", "create_time").where(condition)
		cmdMsg.format match {
			case "day" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()
				rddlogs.createOrReplaceTempView("nginx_base_log")
				val datadf = spark.sql("select create_time, count(remoteaddr) as num from nginx_base_log group by create_time, remoteaddr")
				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("create_time", "num"))).collect()
				var tepmap = new HashMap[String, Long]
				result.foreach(x => {
					val key = x.get("create_time").get.asInstanceOf[String]
					if (tepmap.get(key).isEmpty) {
						tepmap.put(key, 1)
					} else {
						val tepv = tepmap.get(key)
						tepmap.put(key, tepv.get + 1)
					}
				})
				var map = new HashMap[String, HashMap[String, String]]
				tepmap.foreach(tep => {
					var tmap = new HashMap[String, String]
					tmap.put("totalnum", tep._2.toString())
					map.put(tep._1, tmap)
				})
				JsonUtil.toJson(map)
			case "hour" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd HH");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()
				rddlogs.createOrReplaceTempView("nginx_base_log")
				val datadf = spark.sql("select create_time, count(remoteaddr) as num from nginx_base_log group by create_time, remoteaddr")
				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("create_time", "num"))).collect()
				var tepmap = new HashMap[String, Long]
				result.foreach(x => {
					val key = x.get("create_time").get.asInstanceOf[String]
					if (tepmap.get(key).isEmpty) {
						tepmap.put(key, 1)
					} else {
						val tepv = tepmap.get(key)
						tepmap.put(key, tepv.get + 1)
					}
				})
				var map = new HashMap[String, HashMap[String, String]]
				tepmap.foreach(tep => {
					var tmap = new HashMap[String, String]
					tmap.put("totalnum", tep._2.toString())
					map.put(tep._1, tmap)
				})
				JsonUtil.toJson(map)
			case "minute" =>
				val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), sdf.format(x.getDate("create_time")))).toDF()
				rddlogs.createOrReplaceTempView("nginx_base_log")
				val datadf = spark.sql("select create_time, count(remoteaddr) as num from nginx_base_log group by create_time, remoteaddr")
				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("create_time", "num"))).collect()
				var tepmap = new HashMap[String, Long]
				result.foreach(x => {
					val key = x.get("create_time").get.asInstanceOf[String]
					if (tepmap.get(key).isEmpty) {
						tepmap.put(key, 1)
					} else {
						val tepv = tepmap.get(key)
						tepmap.put(key, tepv.get + 1)
					}
				})
				var map = new HashMap[String, HashMap[String, String]]
				tepmap.foreach(tep => {
					var tmap = new HashMap[String, String]
					tmap.put("totalnum", tep._2.toString())
					map.put(tep._1, tmap)
				})
				JsonUtil.toJson(map)
			case _ =>
				val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), "")).toDF()
				rddlogs.createOrReplaceTempView("nginx_base_log")
				val datadf = spark.sql("select count(1) as num from nginx_base_log group by remoteaddr")
				implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
				val result = datadf.map(x => x.getValuesMap[Any](List("num", "remoteaddr"))).collect()
				var totalnum = 0l
				result.foreach(tmap => {
					totalnum = totalnum + 1
				})
				var map = new HashMap[String, HashMap[String, String]]
				var tmap = new HashMap[String, String]
				tmap.put("totalnum", totalnum.toString())
				map.put("data", tmap)
				JsonUtil.toJson(map)
		}
	}
}
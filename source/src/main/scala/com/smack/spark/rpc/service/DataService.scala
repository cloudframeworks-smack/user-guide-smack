package com.smack.spark.rpc.service

import scala.collection.JavaConversions._
import org.apache.spark.SparkConf
import com.datastax.spark.connector.{ SomeColumns, _ }
import org.apache.spark.{ SparkConf, SparkContext }
import com.smack.spark.AccessLog
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.Encoder
import org.apache.spark.sql.SparkSession
import com.smack.util.JsonUtil
import com.smack.model.CmdMessage

class DataService {
  val cassandraHost = "cassandra"

  def handlerAvgTime(cmdMsg: CmdMessage): String = {
    val sparkConf = new SparkConf(true)
      .set("spark.cassandra.connection.host", cassandraHost)
      .set("spark.cleaner.ttl", "3600")
      .setMaster("spark://spark_master:7077")
      .setAppName("CassandraConsumer")
    val ssc = new SparkContext(sparkConf)
    val spark = SparkSession
      .builder()
      .appName("Spark SQL")
      .getOrCreate()

    import com.datastax.spark.connector._
    import spark.implicits._
    val condition = "namespace='" + cmdMsg.namespace + "' and servername='" + cmdMsg.serverName + "' and create_time>'" + cmdMsg.start_time + "' and create_time<='" + cmdMsg.end_time + "'"
    val rdd = ssc.cassandraTable("mydb", "nginx_base_log").select("namespace", "servername", "remoteaddr", "request", "status", "upstreamresponsetime").where(condition)
    val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), "")).toDF()

    System.out.println(rddlogs)
    rddlogs.createOrReplaceTempView("nginx_base_log")

    val datadf = spark.sql("select count(1) as totalnum , sum(upstreamresponsetime) as totaltime from nginx_base_log")

    implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
    val result = datadf.map(x => x.getValuesMap[Any](List("totalnum", "totaltime"))).collect()
    var map = Map("namespace" -> cmdMsg.namespace, "servername" -> cmdMsg.serverName)
    if (result.length > 0) {
      val tmap = result(0)
      map = map.updated("avgtime", (tmap.get("totaltime").get.asInstanceOf[Double] / tmap.get("totalnum").get.asInstanceOf[Long]).toString())
      map = map.updated("totalnum", tmap.get("totalnum").get.toString())
      map = map.updated("totaltime", tmap.get("totaltime").get.toString())
    }
    JsonUtil.toJson(map)
  }

  def handlerPv(cmdMsg: CmdMessage): String = {
    val sparkConf = new SparkConf(true)
      .set("spark.cassandra.connection.host", cassandraHost)
      .set("spark.cleaner.ttl", "3600")
      .setMaster("local[4]")
      .setAppName("CassandraConsumer")
    val ssc = new SparkContext(sparkConf)

    val spark = SparkSession
      .builder()
      .appName("Spark SQL")
      .getOrCreate()

    import com.datastax.spark.connector._
    import spark.implicits._
    val condition = "namespace='" + cmdMsg.namespace + "' and servername='" + cmdMsg.serverName + "' and create_time>'" + cmdMsg.start_time + "' and create_time<='" + cmdMsg.end_time + "'"
    val rdd = ssc.cassandraTable("mydb", "nginx_base_log").select("namespace", "servername", "remoteaddr", "request", "status", "upstreamresponsetime").where(condition)
    val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), "")).toDF()

    System.out.println(rddlogs)
    rddlogs.createOrReplaceTempView("nginx_base_log")

    val datadf = spark.sql("select count(1) as totalnum from nginx_base_log")

    implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
    val result = datadf.map(x => x.getValuesMap[Any](List("totalnum"))).collect()
    var map = Map("namespace" -> cmdMsg.namespace, "servername" -> cmdMsg.serverName)
    if (result.length > 0) {
      val tmap = result(0)
      map = map.updated("totalnum", tmap.get("totalnum").get.toString())
    }
    JsonUtil.toJson(map)
  }

  def handlerUv(cmdMsg: CmdMessage): String = {
    val sparkConf = new SparkConf(true)
      .set("spark.cassandra.connection.host", cassandraHost)
      .set("spark.cleaner.ttl", "3600")
      .setMaster("local[4]")
      .setAppName("CassandraConsumer")
    val ssc = new SparkContext(sparkConf)

    val spark = SparkSession
      .builder()
      .appName("Spark SQL")
      .getOrCreate()

    import com.datastax.spark.connector._
    import spark.implicits._
    val condition = "namespace='" + cmdMsg.namespace + "' and servername='" + cmdMsg.serverName + "' and create_time>'" + cmdMsg.start_time + "' and create_time<='" + cmdMsg.end_time + "'"
    val rdd = ssc.cassandraTable("mydb", "nginx_base_log").select("namespace", "servername", "remoteaddr", "request", "status", "upstreamresponsetime").where(condition)
    val rddlogs = rdd.map(x => new AccessLog(x.getString("namespace"), x.getString("servername"), x.getString("remoteaddr"), x.getString("request"), x.getInt("status"), x.getDouble("upstreamresponsetime"), "")).toDF()

    System.out.println(rddlogs)
    rddlogs.createOrReplaceTempView("nginx_base_log")

    val datadf = spark.sql("select count(1) as num, remoteaddr from nginx_base_log group by remoteaddr")

    implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
    val result = datadf.map(x => x.getValuesMap[Any](List("num", "remoteaddr"))).collect()

    var map = Map("namespace" -> cmdMsg.namespace, "servername" -> cmdMsg.serverName)

    result.foreach(tmap => {
      map = map.updated(tmap.get("remoteaddr").get.toString(), tmap.get("num").get.toString())
    })
    JsonUtil.toJson(map)
  }
}
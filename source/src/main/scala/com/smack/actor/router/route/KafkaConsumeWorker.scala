package com.smack.actor.router.route

import akka.actor.Actor
import akka.actor.ActorLogging
import scala.collection.JavaConversions._
import akka.kafka.{ ConsumerSettings, ProducerSettings }
import org.apache.kafka.common.serialization.{ Deserializer, Serializer, StringDeserializer, StringSerializer, ByteArraySerializer, ByteArrayDeserializer }
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import akka.kafka.javadsl.Producer
import akka.stream.javadsl.Source
import java.util.UUID
import java.util.Date
import scala.concurrent.duration._
import com.smack.util.JsonElement
import java.util.HashMap
import akka.actor.ActorRef
import com.smack.model.MsgEvent
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import com.smack.model.MsgKafka

class KafkaConsumeWorker(config: Config, workerRouter: ActorRef) extends Actor with ActorLogging {
  import context.dispatcher
  context.system.scheduler.schedule(1.seconds, 300.seconds, self, "Load")

  val NameSpaceServerPorts = new HashMap[String, String]

  def receive = {
    case "Load" =>
      for (line <- scala.io.Source.fromFile(config.getString("log.port_server")).getLines) {
        val lines = line.trim().split(":")
        if (lines.length == 3) {
          NameSpaceServerPorts.put(lines(0) + ":" + lines(1), lines(2))
        }
      }
      log.info("total server size=" + NameSpaceServerPorts.size())
    case msgKafka: MsgKafka =>
      try {
        val map = new HashMap[String, Any]
        JsonElement.parseValue(msgKafka.data, map)
        val filename = map.get("file_name").toString().replace("-", "")
        val cluster = msgKafka.topic + ":" + filename
        log.info(cluster)
        if (NameSpaceServerPorts.containsKey(cluster)) {
          val message = map.get("message").toString()
          val key = NameSpaceServerPorts.get(cluster)
          val keys = key.split(" ")
          if (keys.length == 2) {
            val evt = MsgEvent(keys(0), keys(1), message)
            workerRouter tell (ConsistentHashableEnvelope(evt, evt), self)
          }
        }
      } catch {
        case t: Throwable => {
          t.printStackTrace()
        }
      }
    case x => log.info("xxxx" + x)
  }
}
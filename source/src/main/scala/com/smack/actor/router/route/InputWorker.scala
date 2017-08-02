package com.smack.actor.router.route

import akka.actor.Actor
import akka.actor.ActorLogging
import scala.collection.JavaConversions._
import com.smack.model.MsgEvent
import akka.kafka.{ ConsumerSettings, ProducerSettings }
import org.apache.kafka.common.serialization.{ Deserializer, Serializer, StringDeserializer, StringSerializer, ByteArraySerializer, ByteArrayDeserializer }
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import akka.kafka.javadsl.Producer
import akka.stream.javadsl.Source
import java.util.UUID
import java.util.Date

class InputWorker(config: Config) extends Actor with ActorLogging {

	val producerSettings = ProducerSettings(context.system, new ByteArraySerializer, new StringSerializer).withBootstrapServers(config.getString("kafka_producer.uri"))
	val kafkaProducer = producerSettings.createKafkaProducer()

	def receive = {
		case evt: MsgEvent =>
			try {
				//log.info(evt.toString())
				val msg = evt.namespace + config.getString("common.sec") + evt.serverName + config.getString("common.sec") + evt.msg
				val message = new ProducerRecord[Array[Byte], String](config.getString("kafka_producer.topic"), msg)
				kafkaProducer.send(message)
				//log.info(message.toString())
			} catch {
				case t: Throwable => log.info(t.getLocalizedMessage)
			}
		case x =>log.info("xxxx" + x)
	}
}
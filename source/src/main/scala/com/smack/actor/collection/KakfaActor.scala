package com.smack.actor.collection

import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import scala.collection.JavaConversions._
import com.smack.model.MsgEvent
import com.typesafe.config.Config
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.kafka.ConsumerMessage.{ CommittableMessage, CommittableOffsetBatch }
import akka.kafka._
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.clients.consumer.{ ConsumerConfig, ConsumerRecord }
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import akka.Done
import scala.concurrent.Future
import scala.util.{ Failure, Success }
import java.util.concurrent.atomic.AtomicLong
import akka.stream.ActorMaterializer
import com.smack.model.MsgKafka

import org.apache.kafka.common.serialization.{ Deserializer, Serializer, StringDeserializer, StringSerializer, ByteArraySerializer, ByteArrayDeserializer }

class KakfaActor(config: Config, topic: String, workerRouter: ActorRef) extends Actor with ActorLogging {
  implicit val ec = context.system.dispatcher
  implicit val m = ActorMaterializer.create(context.system)
  val consumerSettings = ConsumerSettings(context.system, new ByteArrayDeserializer, new StringDeserializer).withBootstrapServers(config.getString("kafka_consumer.uri")).withGroupId("group" + topic)

  val msg = new Msg(workerRouter, topic, sender)
  Consumer.committableSource(consumerSettings, Subscriptions.topics(topic))
    .mapAsync(1) { m =>
      msg.update(m.record.value).map(_ => m)
    }
    .mapAsync(1) { m =>
      m.committableOffset.commitScaladsl()
    }
    .runWith(Sink.ignore)

  def receive = {
    case x => log.info("xxxx" + x)
  }
}

class Msg(workerRouter: ActorRef, topic: String, sender: ActorRef) {

  private val offset = new AtomicLong

  def save(record: ConsumerRecord[Array[Byte], String]): Future[Done] = {
    println(s"DB.save: ${record.value}")
    offset.set(record.offset)
    Future.successful(Done)
  }

  def loadOffset(): Future[Long] =
    Future.successful(offset.get)

  def update(data: String): Future[Done] = {
    val fmsg = MsgKafka(topic, data)
    workerRouter tell (ConsistentHashableEnvelope(fmsg, fmsg), sender)
    Future.successful(Done)
  }
}
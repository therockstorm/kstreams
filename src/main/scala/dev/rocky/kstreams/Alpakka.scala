package dev.rocky.kstreams

import akka.actor.ActorSystem
import akka.kafka._
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer, Producer}
import akka.stream.Materializer
import akka.stream.Materializer.matFromSystem
import akka.stream.scaladsl.{Keep, Source}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.ExecutionContext

object Alpakka extends App {
  private val config = ConfigFactory.load()
  private val name = config.getString("name")
  private val inTopic = "example.in"
  private val bootstrapServers = "localhost:29092"
  implicit private val mat: Materializer = matFromSystem(ActorSystem(name))
  implicit private val ec: ExecutionContext = mat.system.dispatcher
  private val producerSettings =
    ProducerSettings(mat.system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServers)
  private val committerSettings = CommitterSettings(mat.system)
  private val consumerSettings =
    ConsumerSettings(mat.system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId(name)
      .withProperty(AUTO_OFFSET_RESET_CONFIG, "earliest")

//  Source(1 to 5)
//    .map(_.toString)
//    .map(new ProducerRecord[String, String](inTopic, _))
//    .runWith(Producer.plainSink(producerSettings))

  val consumer = Consumer
    .committableSource(consumerSettings, Subscriptions.topics(inTopic))
    .map { msg =>
      println(s"Got ${msg.record.value()}")
      msg.committableOffset
    }
    .toMat(Committer.sink(committerSettings))(Keep.both)
    .mapMaterializedValue(DrainingControl.apply)
    .run()

//  val consumer = Consumer
//    .plainSource(consumerSettings, Subscriptions.topics(inTopic))
//    .map(v => println(s"Got ${v.value}"))
//    .toMat(Sink.ignore)(Keep.left)
//    .run()

  Thread.sleep(5 * 1000)
  consumer.drainAndShutdown()
  mat.system.terminate()
}

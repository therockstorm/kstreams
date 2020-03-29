package dev.rocky.kstreams

import java.lang.Runtime.getRuntime
import java.net.URI
import java.time.Duration
import java.util.Properties

import com.typesafe.config.ConfigFactory
import dev.rocky.cloudevents.v1.{HeaderExtractor, HeaderTransformer}
import dev.rocky.kafka.{LoggingTransformer, ProtobufSerde}
import dev.rocky.protobuf.kstreams.{Event1, Event2}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig._
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.Produced

object KStreams extends App {
  private val config = ConfigFactory.load()
  private val name = config.getString("name")
  implicit private val event1Serde: Serde[Event1] = new ProtobufSerde[Event1](Event1.fromAscii)
  implicit private val event2Serde: Serde[Event2] = new ProtobufSerde[Event2](Event2.fromAscii)
  implicit private val event2Produced: Produced[String, Event2] = Produced.`with`[String, Event2]
  val streamsBuilder = new StreamsBuilder

  // 1. Consume "in" and perform stateless transformations via map
  val in = streamsBuilder.stream[String, Event1]("example.in")
  //   a. Concat "-dog", produce `k: null -> v: Event2`
  in.mapValues(v => Event2(v.a.concat("-dog")))
    .to(
      // Add CloudEvent headers
      new HeaderExtractor[String, Event2](
        "example.out.1",
        Event2,
        URI.create(s"/$name/concatDog")
      )
    )
  //   b. Concat "-cat", produce `k: Event1 -> v: Event2`
  in.map((_, v) => (v, Event2(v.a.concat("-cat"))))
    // `.through` doesn't accept Extractor so must add CloudEvent headers via Transformer
    .transform(() => new HeaderTransformer(Event2, URI.create(s"/$name/concatCat")))
    //   c. Produce to and then consume "out.1", concat "-fish", log headers, and produce `k: null, v: Event2`
    .through("example.out.1")
    .mapValues(v => Event2(v.b.concat("-fish")))
    .transform(() => new LoggingTransformer)
    .to(
      new HeaderExtractor[Event1, Event2](
        "example.out.3",
        Event2,
        URI.create(s"/$name/concatFish")
      )
    )

  // 2. Create KTable word occurrence histogram, produce `word: String -> count: Long`
  in.flatMapValues(_.a.toLowerCase.split("\\W+"))
    .groupBy((_, word) => word)
    .count()
    .toStream
    .to("example.out.2")

  private val p = {
    val p = new Properties()
    p.put(APPLICATION_ID_CONFIG, name)
    p.put(BOOTSTRAP_SERVERS_CONFIG, if (args.length > 0) args(0) else "localhost:9092")
    p.put(NUM_STREAM_THREADS_CONFIG, getRuntime.availableProcessors())
    // Production config
    //p.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE)
    //p.put(REPLICATION_FACTOR_CONFIG, 3)
    p
  }
  val streams = new KafkaStreams(streamsBuilder.build(), p)

  // Clean local state, causing app to rebuild from Kafka each time. Don't do this in production.
  // If app is throwing exceptions, see README for `kafka-streams-application-reset` command.
  streams.cleanUp()
  streams.start()

  sys.ShutdownHookThread(streams.close(Duration.ofSeconds(10)))
}

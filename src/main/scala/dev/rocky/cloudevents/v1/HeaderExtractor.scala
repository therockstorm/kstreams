package dev.rocky.cloudevents.v1

import java.net.URI

import dev.rocky.util.ValueProvider
import org.apache.kafka.streams.processor.{RecordContext, TopicNameExtractor}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

class HeaderExtractor[K, V <: GeneratedMessage](
    topic: String,
    evtType: GeneratedMessageCompanion[V],
    source: URI
) extends TopicNameExtractor[K, V]
    with ValueProvider {
  override def extract(key: K, value: V, context: RecordContext): String = {
    Headers.add(uuid().toString, source, evtType, context.timestamp(), context.headers())
    topic
  }
}

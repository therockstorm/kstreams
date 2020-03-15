package dev.rocky.cloudevents.v1

import java.net.URI

import dev.rocky.util.ValueProvider
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

class HeaderTransformer[K, V <: GeneratedMessage](
    evtType: GeneratedMessageCompanion[V],
    source: URI
) extends Transformer[K, V, KeyValue[K, V]]
    with ValueProvider {
  import org.apache.kafka.streams.scala.ImplicitConversions._
  private var context: ProcessorContext = _

  override def init(context: ProcessorContext): Unit = this.context = context

  override def transform(key: K, value: V): KeyValue[K, V] = {
    Headers.add(uuid().toString, source, evtType, context.timestamp(), context.headers())
    (key, value)
  }

  override def close(): Unit = context = null
}

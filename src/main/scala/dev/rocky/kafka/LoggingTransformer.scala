package dev.rocky.kafka

import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext

class LoggingTransformer[K, V] extends Transformer[K, V, KeyValue[K, V]] {
  import org.apache.kafka.streams.scala.ImplicitConversions._
  private var context: ProcessorContext = _

  override def init(context: ProcessorContext): Unit = this.context = context

  override def transform(key: K, value: V): KeyValue[K, V] = {
    println(s"--- k=$key v=$value")
    context.headers().forEach(hs => println(s"${hs.key()}: ${new String(hs.value())}"))
    (key, value)
  }

  override def close(): Unit = context = null
}

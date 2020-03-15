package dev.rocky.kafka

import java.nio.charset.StandardCharsets.US_ASCII

import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}
import scalapb.GeneratedMessage

class ProtobufSerde[A >: Null <: GeneratedMessage](fromAscii: String => A) extends Serde[A] {
  object Ser extends Serializer[A] {
    override def serialize(topic: String, data: A): Array[Byte] =
      Option(data).map(_.toProtoString.getBytes).orNull
  }

  override def serializer(): Serializer[A] = Ser

  object De extends Deserializer[A] {
    override def deserialize(topic: String, data: Array[Byte]): A =
      Option(data).map(d => fromAscii(new String(d, US_ASCII))).orNull
  }

  override def deserializer(): Deserializer[A] = De
}

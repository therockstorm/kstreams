package dev.rocky.cloudevents.v1

import java.net.URI
import java.time.Instant

import org.apache.kafka.common.header
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

object Headers {
  // See https://github.com/cloudevents/spec/blob/master/kafka-protocol-binding.md
  def add[T <: GeneratedMessage](
      id: String,
      source: URI,
      evtType: GeneratedMessageCompanion[T],
      timestamp: Long,
      headers: header.Headers
  ): Unit =
    add(
      headers,
      Seq(
        Header("ce_specversion", "1.0"),
        // source + id MUST be unique for distinct events
        Header("ce_id", id),
        Header("ce_source", source),
        Header("ce_time", Instant.ofEpochMilli(timestamp)),
        Header("ce_type", evtType.javaDescriptor.getFullName),
        // https://github.com/googleapis/google-api-python-client/blob/master/googleapiclient/model.py#L338-L339
        Header("content-type", "application/x-protobuf")
      )
    )

  final case class Header(key: String, value: AnyRef)
  private def add(hs: header.Headers, toAdd: Seq[Header]): Unit =
    toAdd.foreach { h =>
      hs.remove(h.key)
      hs.add(h.key, Option(h.value).map(_.toString).getOrElse("").getBytes)
    }
}

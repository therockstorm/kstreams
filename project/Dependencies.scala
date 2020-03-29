import sbt._
import scalapb.compiler.Version

object Dependencies {
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.6.4"
  lazy val akkaStreamKafka = "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.2"
  lazy val configLib = "com.typesafe" % "config" % "1.4.0"
  lazy val kafkaStreamsScala = "org.apache.kafka" %% "kafka-streams-scala" % "2.4.1"
  lazy val scalaPbRuntime = "com.thesamet.scalapb" %% "scalapb-runtime" % Version.scalapbVersion % "protobuf"
}

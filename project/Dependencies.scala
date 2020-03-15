import sbt._

object Dependencies {
  lazy val configLib = "com.typesafe" % "config" % "1.4.0"
  lazy val kafkaStreamsScala = "org.apache.kafka" %% "kafka-streams-scala" % "2.4.1"
  lazy val scalaPbRuntime = "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
}

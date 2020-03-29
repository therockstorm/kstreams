import Dependencies._

ThisBuild / version := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    coverageExcludedPackages := "<empty>;dev.rocky.kstreams.Main.scala",
    libraryDependencies ++= Seq(
      akkaStream,
      akkaStreamKafka,
      configLib,
      kafkaStreamsScala,
      scalaPbRuntime
    ),
    name := "kstreams",
    PB.targets in Compile := Seq(scalapb.gen(flatPackage=true) -> (sourceManaged in Compile).value),
  )

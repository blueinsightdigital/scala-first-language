ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

val zioSchemaVersion = "0.4.9"
val zioVersion = "2.0.10"
val zioOpenAiVersion = "0.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "funscala2023",
    idePackagePrefix := Some("digital.blueinsight.funscala2023")
  )

libraryDependencies += "io.cequence" %% "openai-scala-client" % "0.4.1"
libraryDependencies += ("com.typesafe.akka" %% "akka-actor" % "2.6.20").cross(
  CrossVersion.for3Use2_13
)
libraryDependencies += "io.circe" %% "circe-core" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-parser" % "0.14.1"
libraryDependencies += "org.typelevel" %% "cats-free" % "2.10.0"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "23.1.0.0"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-openai" % zioOpenAiVersion,
  "dev.zio" %% "zio-schema" % zioSchemaVersion,
  "dev.zio" %% "zio-schema-json" % zioSchemaVersion,
  "com.lihaoyi" %% "pprint" % "0.8.1"
)

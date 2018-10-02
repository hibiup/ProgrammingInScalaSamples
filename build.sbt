name := "ProgrammingInScala"

version := "0.1"

scalaVersion := "2.12.6"

val scalaTestVersion = "3.0.4"
val akkaVersion = "2.5.14"

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
    "org.scala-lang.modules" %% "scala-async" % "0.9.6"
)
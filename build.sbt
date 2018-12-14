name := "ProgrammingInScala"

version := "0.1"

scalaVersion := "2.12.6"

val scalaTestVersion = "3.0.4"
val akkaVersion = "2.5.14"

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
    "org.scala-lang.modules" %% "scala-async" % "0.9.6" /*,
    "org.scalaz" %% "scalaz-core" % "7.2.18",
    "org.scalaz" %% "scalaz-zio" % "0.5.0"*/
)
scalacOptions ++= Seq("-Xplugin-require:macroparadise")
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
//scalacOptions ++= Seq("-feature")

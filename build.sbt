name := "appuchi"
organization := "yali.com"
version := "0.1.0-SNAPSHOT"

assemblyJarName in assembly := "appuchi-server.jar"

lazy val akkaHttpVersion = "10.1.0"
lazy val akkaVersion = "2.5.11"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
)
assemblyMergeStrategy in assembly := {
  case PathList("org", "slf4j", xs@_*) => MergeStrategy.first
  case x => (assemblyMergeStrategy in assembly).value(x)
}
lazy val scalikeJDBCSettings = Seq(
  libraryDependencies ++= Seq(
    "org.postgresql" % "postgresql" % "42.1.1",
    "org.scalikejdbc" %% "scalikejdbc" % "3.3.1",
    "org.scalikejdbc" %% "scalikejdbc-config" % "3.3.1",
    "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.3"
  )
)
lazy val root = (project in file(".")).
        settings(
            inThisBuild(List(
                organization := "com.yali",
                scalaVersion := "2.12.3"
            )),
            name := "server",
            libraryDependencies ++= Seq(

                "com.h2database" % "h2" % "1.4.191",

                // Should not be required, but let's see if it works
                "org.flywaydb" % "flyway-core" % "4.2.0",

                "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
                "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
                "com.typesafe.akka" %% "akka-stream" % akkaVersion,
                "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0",
                "ch.qos.logback" % "logback-classic" % "1.2.3"  exclude("org.slf4j", "slf4j-api"),
                "com.typesafe.akka" % "akka-slf4j_2.12" % "2.5.6",
                "com.typesafe.scala-logging" %% "scala-logging" % "3.7.0",
                "com.h2database" % "h2" % "1.4.191",
                "org.postgresql" % "postgresql" % "42.1.1",
                "org.typelevel" %% "cats-core" % "1.0.1",
                "io.circe" % "circe-core_2.12" % "0.9.1",
                "io.circe" % "circe-generic_2.12" % "0.9.1",
                "io.circe" % "circe-parser_2.12" % "0.9.1",
                "io.circe" % "circe-java8_2.12" % "0.9.1",
                "org.scalikejdbc" % "scalikejdbc_2.12" % "3.3.1",
                "com.pauldijou" %% "jwt-circe" % "0.15.0",
                "de.heikoseeberger" % "akka-http-circe_2.12" % "1.20.0",
                "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
                "org.scalatest" %% "scalatest" % "3.0.3" % Test,
                "org.scalikejdbc" %% "scalikejdbc-test" % "3.1.0" % Test,
                "org.mockito" % "mockito-all" % "1.10.19" % "test"
            )
        ).settings(scalikeJDBCSettings)

enablePlugins(ScalikejdbcPlugin)





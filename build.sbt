import java.lang.management.ManagementFactory
import java.lang.System.getProperty
import java.nio.charset.StandardCharsets.UTF_8

import sbt.{Keys, File, Tests}

import scala.collection.JavaConverters._

net.virtualvoid.sbt.graph.Plugin.graphSettings

organization := "com.casualmiracles"
name := "symplegades"
version := Option(getProperty("boost.version")).getOrElse("dev.build")
scalaVersion := "2.11.7"
scalaBinaryVersion := "2.11"
incOptions := incOptions.value.withNameHashing(true)

val basicScalacOptions = Seq(
  "-target:jvm-1.8",
  "-language:postfixOps", "-language:higherKinds", "-language:implicitConversions",
  "-deprecation",
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Xlint:adapted-args",    // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

scalacOptions in (Test, Keys.test) ++= basicScalacOptions
scalacOptions in (Compile, Keys.compile) ++= basicScalacOptions ++ Seq("-Ywarn-value-discard") //, "-Xfatal-warnings")

externalResolvers := Seq(Resolver.defaultLocal)

val libs = {
  val scalazVersion      = "7.1.4"

  Seq(
    "org.scalaz"                   %% "scalaz-core"                       % scalazVersion,
    "org.scalaz"                   %% "scalaz-effect"                     % scalazVersion,
    "io.argonaut"                  %% "argonaut"                          % "6.1",
    "com.barclays"                 %% "wartremover-annotations"           % "0.12.6",
    "org.scalatest"                %% "scalatest"                         % "2.2.5"                % "test",
    "org.scalacheck"               %% "scalacheck"                        % "1.12.5"               % "test"
  )
}

libraryDependencies ++= libs

unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil

unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil

initialize := {
  val encoding: String = sys.props("file.encoding")
  assert(encoding == "UTF-8", s"File encoding must be UTF-8 but was $encoding")
}

compile <<= (compile in Compile) map { result =>
  require(getProperty("file.encoding") == UTF_8.toString, s"$UTF_8 required. Please set java system property file.encoding")
  result
}

parallelExecution in Test := true
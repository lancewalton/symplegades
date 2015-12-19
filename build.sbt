organization := "com.casualmiracles"
name := "symplegades"
version := "0.0.1"
scalaVersion := "2.11.7"
scalaBinaryVersion := "2.11"
incOptions := incOptions.value.withNameHashing(true)

resolvers ++= Seq(
  Resolver.sonatypeRepo("public")
)

val basicScalacOptions = Seq(
  "-target:jvm-1.8",
  "-language:postfixOps", "-language:higherKinds", "-language:implicitConversions",
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Xlint:adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xfatal-warnings"
)

scalacOptions in (Test, Keys.test) ++= basicScalacOptions
scalacOptions in (Compile, Keys.compile) ++= basicScalacOptions ++ Seq("-Ywarn-value-discard")

val libs = {

  val argonautVersion = "6.2-SNAPSHOT"

  Seq(
    "org.spire-math"    %% "cats"             % "0.3.0",
    "io.argonaut"       %% "argonaut"         % argonautVersion,
    "io.argonaut"       %% "argonaut-monocle" % argonautVersion,
    "org.scalatest"     %% "scalatest"        % "2.2.5"   % "test",
    "org.scalacheck"    %% "scalacheck"       % "1.12.5"  % "test"
  )
}

libraryDependencies ++= libs

unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil

unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil

parallelExecution in Test := true

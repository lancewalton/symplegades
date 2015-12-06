organization := "com.casualmiracles"
name := "symplegades"
version := "0.0.1"
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
  "-Xfuture",
  "-Xfatal-warnings"
)

scalacOptions in (Test, Keys.test) ++= basicScalacOptions
scalacOptions in (Compile, Keys.compile) ++= basicScalacOptions ++ Seq("-Ywarn-value-discard")

val libs = {
  val scalazVersion      = "7.1.5"

  Seq(
    "org.scalaz"                   %% "scalaz-core"                       % scalazVersion,
    "io.argonaut"                  %% "argonaut"                          % "6.1",
    "org.scalatest"                %% "scalatest"                         % "2.2.5"                % "test",
    "org.scalacheck"               %% "scalacheck"                        % "1.12.5"               % "test"
  )
}

libraryDependencies ++= libs

unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil

unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil

parallelExecution in Test := true

enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

name := "laminar-vega-demo"

version := "0.1.0"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "com.raquo" %%% "laminar" % "0.14.5",
  "dev.zio" %%% "zio" % "2.0.18",
  "org.scala-js" %%% "scalajs-dom" % "2.4.0"
)

scalaJSUseMainModuleInitializer := true

// Enable Java time support for Scala.js
libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.5.0"

npmDependencies in Compile ++= Seq(
  "vega" -> "5.25.0",
  "vega-lite" -> "5.9.2",
  "vega-embed" -> "6.21.0"
)

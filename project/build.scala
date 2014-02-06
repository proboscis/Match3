import sbt._

import Keys._
import org.scalasbt.androidplugin._
import org.scalasbt.androidplugin.AndroidKeys._
import sbtassembly.Plugin._
import AssemblyKeys._
object Constants{
  val scalazVersion = "7.0.4"
  val liftVersion = "2.5"
  val sVersion = "2.10.3"
  val gdxVersion = "1.0-SNAPSHOT"
}
object Settings {
  import Constants._
  lazy val common = Defaults.defaultSettings ++ Seq (
    version := "0.2",
    scalaVersion := sVersion,
    resolvers += "spray" at "http://repo.spray.io/"//this is required to use spray
    ,
    {
      libraryDependencies ++=  Seq(
        "org.scalaz" %% "scalaz-core" % scalazVersion,
        "org.scalaz" %% "scalaz-effect" % scalazVersion,
        "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
        "com.chuusai" % "shapeless" % "2.0.0-M1" cross CrossVersion.full,
        "com.github.scaldi" %% "scaldi" % "0.2",
        "io.spray" %%  "spray-json" % "1.2.5")
      },
    //scalacOptions ++=Seq("-optimize"),
    //scalacOptions ++=Seq("-Xprint:lambdalift"),
    scalacOptions ++=Seq("-feature"),
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),//this is required to avoid "bad file magic" problems
    javacOptions ++= Seq("-encoding","utf8")//this is required to avoid encoding issues with japanese comments in Windows
    ,
    resolvers += Resolver.sonatypeRepo("snapshots")
    ,
    addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise" % "2.0.0-SNAPSHOT" cross CrossVersion.full)
    ,
    updateLibgdxTask
    )
  lazy val desktop = Settings.common ++  Seq (
    fork in Compile := true
    ) 
  lazy val android = Settings.common ++
  AndroidProject.androidSettings ++
  AndroidMarketPublish.settings ++ Seq (
    platformName in Android := "android-11",
    keyalias in Android := "change-me",
    mainAssetsPath in Android := file("common/src/main/resources"),
    unmanagedBase <<= baseDirectory( _ /"src/main/libs" ),
    proguardOption in Android := {
      import scala.io.Source
      val options = Source.fromFile("./proguardOptions.txt").getLines.mkString(" ")
      options
    }
    )
  val updateLibgdx = TaskKey[Unit]("update-gdx", "Updates libgdx")

  val updateLibgdxTask = updateLibgdx <<= streams map { (s: TaskStreams) =>
    import Process._
    import java.io._
    import java.net.URL
    import java.util.regex.Pattern

    // Declare names
    val baseUrl = "http://libgdx.badlogicgames.com/nightlies"
    val gdxName = "libgdx-nightly-latest"

    // Fetch the file.
    s.log.info("Pulling %s" format(gdxName))
    s.log.warn("This may take a few minutes...")
    val zipName = "%s.zip" format(gdxName)
    val zipFile = new java.io.File(zipName)
    val url = new URL("%s/%s" format(baseUrl, zipName))
    IO.download(url, zipFile)

    // Extract jars into their respective lib folders.
    s.log.info("Extracting common libs")
    val commonDest = file("common/lib")
    val commonFilter = new ExactFilter("gdx.jar")
    IO.unzip(zipFile, commonDest, commonFilter)

    s.log.info("Extracting desktop libs")
    val desktopDest = file("desktop/lib")
    val desktopFilter = new ExactFilter("gdx-natives.jar") |
    new ExactFilter("gdx-backend-lwjgl.jar") |
    new ExactFilter("gdx-backend-lwjgl-natives.jar")
    IO.unzip(zipFile, desktopDest, desktopFilter)

    s.log.info("Extracting ios libs")
    val iosDest = file("ios/libs")
    val iosFilter = GlobFilter("ios/*")
    IO.unzip(zipFile, iosDest, iosFilter)

    s.log.info("Extracting android libs")
    val androidDest = file("android/src/main/libs")
    val androidFilter = new ExactFilter("gdx-backend-android.jar") |
    new ExactFilter("armeabi/libgdx.so") |
    new ExactFilter("armeabi/libandroidgl20.so") |
    new ExactFilter("armeabi-v7a/libgdx.so") |
    new ExactFilter("armeabi-v7a/libandroidgl20.so")
    IO.unzip(zipFile, androidDest, androidFilter)

    // Destroy the file.
    zipFile.delete
    s.log.info("Update complete")
  }
}

object LibgdxBuild extends Build {
  import Constants._
  lazy val macros = Project(
    "macros",
    file("macros"),
    settings = Settings.common ++ Seq(libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _))
    )

  val common = Project (
    "common",
    file("common"),
    settings = Settings.common
    ) dependsOn macros

  lazy val desktop = Project (
    "desktop",
    file("desktop"),
    settings = Settings.desktop ++ assemblySettings ++ Seq(
      libraryDependencies ++= Seq(
        "com.googlecode.scalascriptengine" % "scalascriptengine" % ("1.3.7-"+sVersion),
        "org.scala-lang" % "scala-compiler" % sVersion,
        "org.scala-lang" % "scala-reflect" % sVersion,
        "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
        "com.github.scopt" %% "scopt" % "3.1.0"
        )
      )
    ) dependsOn common 

  lazy val android = Project (
    "android",
    file("android"),
    settings = Settings.android
    ) dependsOn common
}

import sbt._
import sbt.Keys._
import android.Keys._
import android.Dependencies.{apklib,aar,AutoLibraryProject}
import scala.io.Source

object LibgdxBuild extends Build {
  val sVersion = "2.10.3"//scala version
  val scalazVersion = "7.0.4"
  lazy val droid = Project(id="android",base=file("android")) settings (androidSettings :_*) dependsOn(core)
  lazy val desktop = Project(id="desktop",base=file("desktop")) settings (desktopSettings :_*) dependsOn(core)
  lazy val core = Project(id="core",base=file("core")) settings(coreSettings:_*) dependsOn(macro)
  lazy val macro = Project(id="macro",base=file("macro")) settings(macroSettings:_*)
  lazy val root = Project(id="root",base=file(".")) settings(rootSettings:_*) aggregate(macro,core,desktop,droid)
  lazy val tools = Project(id="tools",base=file("tools")) settings (toolsSettings :_*)
  lazy val commonSettings = (watchSources ~= { _.filterNot(_.isDirectory) }) ++ Seq (
    scalaVersion := sVersion,
    scalacOptions ++=Seq("-feature","-encoding","utf8"),
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),//this is required to avoid "bad file magic" problems
    javacOptions ++= Seq("-encoding","utf8")//this is required to avoid encoding issues with japanese comments in Windows
    )
  lazy val androidSettings = commonSettings ++ android.Plugin.androidBuild(core) ++ Seq(
    platformTarget in Android := "android-11",
    dexMaxHeap in Android := "1408m",
    /* //ProguardCache is disabled in my plugin!!!
    proguardCache in Android ++= Seq(
      ProguardCache("scalaz") % "org.scalaz" ,
      ProguardCache("android") % "android",
      ProguardCache("java") % "java",
      ProguardCache("javax") % "javax",
      ProguardCache("spray") % "io.spray",
      ProguardCache("shapeless") % "com.chuusai"
      )
  */
    proguardCache in Android := Seq(),
    proguardOptions in Android ++= Source.fromFile("./proguardOptions.txt")("UTF-8").getLines.toSeq,
      localProjects in Android <+= (baseDirectory) {
        b => AutoLibraryProject(b/".."/"play")
      }
    )
  lazy val desktopSettings = commonSettings ++ Seq(
    //this creates new jvm for the task
    fork in Compile := true,
    //set android assets folder as working directory
    baseDirectory in run <<= baseDirectory((base:File)=>base / "../android/assets"),
    libraryDependencies ++= Seq(
      "com.googlecode.scalascriptengine" % "scalascriptengine" % ("1.3.7-"+sVersion),
      "org.scala-lang" % "scala-compiler" % sVersion,
      "org.scala-lang" % "scala-reflect" % sVersion,
      "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
      "com.github.scopt" %% "scopt" % "3.1.0"
      )
    )
  lazy val coreSettings = commonSettings ++ Seq(
    exportJars in Compile := true,
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % scalazVersion,
      "org.scalaz" %% "scalaz-effect" % scalazVersion,
      "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
      "com.chuusai" % "shapeless" % "2.0.0-M1" cross CrossVersion.full,
      "io.spray" %%  "spray-json" % "1.2.5"
      )
    )
  lazy val macroSettings = commonSettings ++ Seq(
    exportJars in Compile:= true,//dont forget to export this project as a jar!
    libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)
    )
  lazy val rootSettings = commonSettings ++ android.Plugin.androidCommands
  import ToolTasks._
  lazy val toolsSettings = commonSettings ++ Seq(
      fork in run := true,
      hieroTask,
      distanceFieldTask,
      particleEditorTask
    )
}
object ToolTasks{
    lazy val hieroKey = TaskKey[Unit]("hiero")
    lazy val hieroTask = fullRunTask(hieroKey,Test,"com.badlogic.gdx.tools.hiero.Hiero","")
    lazy val distanceFieldKey = InputKey[Unit]("distanceField")
    lazy val distanceFieldTask = fullRunInputTask(distanceFieldKey,Test,"com.badlogic.gdx.tools.distancefield.DistanceFieldGenerator")
    lazy val particleEditorKey = InputKey[Unit]("particle")
    lazy val particleEditorTask = fullRunInputTask(particleEditorKey,Test,"com.badlogic.gdx.tools.particleeditor.ParticleEditor")

}
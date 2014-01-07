package com.glyph.scala.lib.util.reactive

import com.glyph.scala.lib.util.Logging
import scala.reflect.io.{Directory, Path}
import java.io.File
import com.googlecode.scalascriptengine.{SourcePath, Config, ScalaScriptEngine}
import scala.util.{Failure, Success, Try}
import scala.reflect.ClassTag
import com.badlogic.gdx.{Application, Gdx}
import com.glyph.scala.lib.libgdx.GdxUtil

/**
 * @author glyph
 */
class ClassScripter(srcDir: String, outDir: String, classDir: String) extends SourceChecker(srcDir, outDir) with Logging {
  val sourcePath = outDir
  val sourceDir = new File(sourcePath)
  Directory.apply(Path(sourcePath)).deleteRecursively()
  new File(classDir).mkdirs()
  val sse = new ScalaScriptEngine(Config(SourcePath(sourceDir, new File(classDir)) :: Nil))
  sse.deleteAllClassesInOutputDirectory()
  try {
    sse.refresh
  } catch {
    case e: Throwable => e.printStackTrace()
  }

  override def onFileChange(file: File): Unit = {
    super.onFileChange(file)
    log("fileChange")
    try {
      sse.refresh
    } catch {
      case e: Throwable => e.printStackTrace()
    }
  }

  def try2Opt[T](t: Try[T]): Option[T] = t match {
    case Success(s) => Some(s)
    case Failure(e: IllegalStateException) => None
    case Failure(f) => f.printStackTrace(); None
  }

  def getClass[I, T <: I : ClassTag]: Varying[Option[Class[I]]] = {
    log("getClass")
    super.get[T].map {
      file => {
        log(("mapFileToClass", file.getAbsolutePath))
        val tag = implicitly[ClassTag[T]]
        val canon = tag.runtimeClass.getCanonicalName
        val simple = tag.runtimeClass.getSimpleName
        val replaced = canon.replace(simple, prefix + simple)
        log("replaced", replaced)
        val opt = try2Opt(Try(sse.get[I](replaced)))
        if (opt.isDefined) opt
        else {
          try2Opt(Try(Class.forName(implicitly[ClassTag[T]].runtimeClass.getCanonicalName).asInstanceOf[Class[I]]))
        }
      }
    }
  }

  def getClass[I](clsName: String): Varying[Option[Class[I]]] = {
    super.get[I](clsName).map {
      file => {
        log(("mapFileToClass", file.getAbsolutePath))
        val cls = Class.forName(clsName)
        val canon = cls.getCanonicalName
        val simple = cls.getSimpleName
        val replaced = canon.replace(simple, prefix + simple)
        log("replaced", replaced)
        val opt = try2Opt(Try(sse.get[I](replaced)))
        if (opt.isDefined) opt
        else {
          try2Opt(Try(cls.asInstanceOf[Class[I]]))
        }
      }
    }
  }

  start()
}

object VClass {

  import Application.ApplicationType._

  var srcDir = "./src/main/scala"
  var mirrorDir = "./.changed"
  var mirrorClassDirName = ".classes"
  lazy val scripter = if (Gdx.app == null) {
    new ClassScripter(srcDir, mirrorDir, mirrorDir + "/" + mirrorClassDirName)
  } else Gdx.app.getType match {
    case Android => throw new RuntimeException("cannot use VClass on android!")
    case Desktop => new ClassScripter(srcDir, mirrorDir, mirrorDir + "/" + mirrorClassDirName)
    case _ => throw new RuntimeException("VClass is not supported on this device")
  }

  def handleTry[T](t: Try[T]): Option[T] = t match {
    case Success(s) => Some(s)
    case Failure(f) => f.printStackTrace(); None
  }

  def apply[I, T <: I : ClassTag]: Varying[Option[Class[I]]] = apply[I](implicitly[ClassTag[T]].runtimeClass.getCanonicalName)

  def apply[I](className: String): Varying[Option[Class[I]]] = {
    def desktop = new Varying[Option[Class[I]]] with Reactor {
      val varying = scripter.getClass[I](className)
      reactVar(varying) {
        c => if (Gdx.app != null) GdxUtil.post(notifyObservers(c))
        else {
          notifyObservers(c)
        }
      }

      def current: Option[Class[I]] = varying()
    }
    def nop = Var(handleTry(Try(Class.forName(className).asInstanceOf[Class[I]])))
    if (Gdx.app == null) desktop
    else {
      Gdx.app.getType match {
        case Android => nop
        case Desktop => desktop
        case iOS => nop
        case WebGL => nop
      }
    }
  }
}
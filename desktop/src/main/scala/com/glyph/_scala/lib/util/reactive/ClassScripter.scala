package com.glyph._scala.lib.util.reactive

import com.glyph._scala.lib.util.Logging
import java.io.File
import scala.reflect.io.{Path, Directory}
import com.googlecode.scalascriptengine.{SourcePath, Config, ScalaScriptEngine}
import scala.util.{Failure, Success, Try}
import scala.reflect.ClassTag

/**
 * @author glyph
 */
class ClassScripter(srcDir: String, outDir: String, classDir: String) extends SourceChecker(srcDir, outDir) with Logging with VClass {
  log("created a ClassScripter")
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

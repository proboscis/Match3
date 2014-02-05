package com.glyph._scala.lib.util.reactive

import scala.util.{Failure, Success, Try}
import scala.reflect.ClassTag
import com.badlogic.gdx.{Application, Gdx}
import com.glyph._scala.lib.libgdx.GdxUtil
import com.badlogic.gdx.Application.ApplicationType

trait VClass {
  def getClass[I, T <: I : ClassTag]: Varying[Option[Class[I]]]
  def getClass[I](clsName: String): Varying[Option[Class[I]]]
}

object VClass {

  import Application.ApplicationType._

  var srcDir = "./src/main/scala"
  var mirrorDir = "./.changed"
  var mirrorClassDirName = ".classes"

  lazy val desktopImpl = Class.forName("com.glyph.scala.lib.util.reactive.ClassScripter").getConstructor(classOf[String], classOf[String], classOf[String]).newInstance(srcDir, mirrorDir, mirrorDir + "/" + mirrorClassDirName).asInstanceOf[VClass]

  lazy val scripter = if (Gdx.app == null) {
    throw new RuntimeException("cannot create VClass the application is created")
  } else Gdx.app.getType match {
    case Android => throw new RuntimeException("cannot use VClass on android!")
    case Desktop => desktopImpl
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
        case ApplicationType.Android => nop
        case ApplicationType.Desktop => desktop
        case ApplicationType.iOS => nop
        case ApplicationType.WebGL => nop
      }
    }
  }
}
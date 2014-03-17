package com.glyph._scala.lib.util.reactive

import scala.util.{Failure, Success, Try}
import scala.reflect.ClassTag
import com.badlogic.gdx.{Application, Gdx}
import com.glyph._scala.lib.libgdx.GdxUtil
import com.badlogic.gdx.Application.ApplicationType
import scalaz.syntax.typelevel.HLists
import scalaz.typelevel.HList
import com.glyph._scala.lib.util.reactive.VClass.Typed

trait VClassGenerator {
  def getClass[Interface, Target <: Interface : ClassTag]: Varying[Try[Class[Interface]]]
  def getClass[I](clsName: String): Varying[Try[Class[I]]]
}

class VClass[Interface](value:Varying[Try[Class[Interface]]]) extends Copied(value){
  import scalaz._
  import Scalaz._
  //TODO this should be done in HList with Classes, but it's too time consuming and this is not my job to do.
  def newInstance(params:Typed[_<:AnyRef]*):Varying[Try[Interface]] =
    value.map(_.map(cls=>Try(cls.getConstructor(params.map(_.cls):_*).newInstance(params.map(_.value):_*))).flatten)
}
object VClass{
  case class Typed[T<:AnyRef:Class](value:T){
    val cls = implicitly[Class[T]]
  }
  import Application.ApplicationType._

  var srcDir = "../../core/src/main/scala"
  var mirrorDir = "../../.changed"
  var mirrorClassDirName = ".classes"

  lazy val desktopImpl = Class.forName("com.glyph._scala.lib.util.reactive.ClassScripter").getConstructor(classOf[String], classOf[String], classOf[String]).newInstance(srcDir, mirrorDir, mirrorDir + "/" + mirrorClassDirName).asInstanceOf[VClassGenerator]

  lazy val scripter = if (Gdx.app == null) {
    throw new RuntimeException("cannot create VClass the application is created")
  } else Gdx.app.getType match {
    case Android => throw new RuntimeException("cannot use VClass on android!")
    case Desktop => desktopImpl
    case _ => throw new RuntimeException("VClass is not supported on this device")
  }

  def apply[Interface, T <: Interface : Class] = apply[Interface](implicitly[Class[T]].getCanonicalName)

  def apply[Interface](className: String) = new VClass({
    def desktop = new Varying[Try[Class[Interface]]] with Reactor {
      val varying = scripter.getClass[Interface](className)
      reactVar(varying) {
        c => if (Gdx.app != null) GdxUtil.post(notifyObservers(c))
        else {
          notifyObservers(c)
        }
      }
      def current: Try[Class[Interface]] = varying()
    }
    def nop = Var(Try(Class.forName(className).asInstanceOf[Class[Interface]]))
    if (Gdx.app == null) desktop
    else {
      Gdx.app.getType match {
        case ApplicationType.Android => nop
        case ApplicationType.Desktop => desktop
        case ApplicationType.iOS => nop
        case ApplicationType.WebGL => nop
      }
    }
  })
}
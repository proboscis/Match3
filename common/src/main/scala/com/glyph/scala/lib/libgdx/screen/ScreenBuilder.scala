package com.glyph.scala.lib.libgdx.screen

import com.badlogic.gdx.assets.AssetManager
import scalaz.Scalaz._
import scalaz._
import scala.util.control.Exception._
import scala.io.Source
import com.badlogic.gdx.Screen
import net.liftweb.json._
import net.liftweb.json.Serialization._
import net.liftweb.json.TypeInfo
import net.liftweb.json.MappingException
/**
 * @author glyph
 */
trait ScreenBuilder {
  type FileName = String
  def requiredAssets: Set[(Class[_], Array[FileName])]
  def create(assetManager: AssetManager): Screen
}

object ScreenBuilder {
  implicit val JsonFormat = Serialization.formats(NoTypeHints) + ClassSerializer
  case class ScreenConfig(screenClass: Class[_], assets: Set[(Class[_], Array[String])])
  def writeConfig(config: ScreenConfig) = write(config)
  type Vnelt[T] = ValidationNel[Throwable, T]
  def createFromJson(filePath: String): Vnelt[ScreenBuilder] = for {
    json <- readFile(filePath)
    config<- jsonToConfig(json)
    constructor <- classToConstructor(config.screenClass)
  } yield new ScreenBuilder {
      def create(assetManager: AssetManager): Screen = constructor(assetManager)
      def requiredAssets = config.assets
    }
  def jsonToConfig(json:String):Vnelt[ScreenConfig] = allCatch.either{
    read[ScreenConfig](json)
  }
  private def readFile(path: String): Vnelt[String] = allCatch.either {
    Source.fromFile(path).getLines().mkString("\n")
  }
  private def classToConstructor(clazz: Class[_]): Vnelt[AssetManager => Screen] = allCatch.either {
    if (!classOf[Screen].isAssignableFrom(clazz)) throw new RuntimeException("specified class is not a subclass of Screen! : " + clazz.getCanonicalName)
    val constructor = clazz.getConstructor(classOf[AssetManager])
    (am: AssetManager) => constructor.newInstance(am).asInstanceOf[Screen]
  }
  object ClassSerializer extends Serializer[Class[_]] {
    private val ClassClass = classOf[Class[_]]
    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Class[_]] = {
      case (TypeInfo(ClassClass, _), json) => json match {
        case JObject(JField("class", JString(className)) :: Nil) => Class.forName(className)
        case x => throw new MappingException("Can't convert " + x + " to class")
      }
    }
    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: Class[_] =>
        JObject(JField("class", JString(x.getCanonicalName)) :: Nil)
    }
  }
  private implicit def eitherToVnelt[T](either: Either[Throwable, T]): Validation[NonEmptyList[Throwable], T] = either fold(_.failNel, _.success)
}
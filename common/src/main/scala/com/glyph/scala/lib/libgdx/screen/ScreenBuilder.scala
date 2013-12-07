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
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder.{Assets, Vnelt}
import scala.util.Try

/**
 * @author glyph
 */
trait ScreenBuilder {
  //TODO serialization is the hardest thing to do in android, you know
  type FileName = String
  def requiredAssets: Set[(Class[_], Array[FileName])]
  def create(assetManager: AssetManager): Screen
}

object ScreenBuilder {
  implicit val JsonFormat = Serialization.formats(NoTypeHints) + ClassSerializer
  type Vnelt[T] = ValidationNel[Throwable, T]
  type Assets = Set[(Class[_], Array[String])]

  private implicit def eitherToVnelt[T](either: Either[Throwable, T]): Validation[NonEmptyList[Throwable], T] = either fold(_.failNel, _.success)
  private implicit def tryToVnel[T](t:Try[T]):ValidationNel[Throwable,T] = t match{
    case scala.util.Success(s) => s.successNel
    case scala.util.Failure(f) => f.failNel
  }
  def apply(assets:Assets)(constructor:AssetManager => Screen):ScreenBuilder =new ScreenBuilder{
    def requiredAssets: Assets = assets
    def create(assetManager: AssetManager): Screen = constructor(assetManager)
  }
  def configToBuilder(config:ScreenConfig)= config.screenClass |> classToConstructor map ScreenBuilder(config.assets)
  def writeConfig(config: ScreenConfig) = write(config)
  def createFromFile(filePath: String) = filePath |> readFile flatMap createFromJson
  def createFromJson(json:String) = json |> jsonToConfig flatMap configToBuilder
  def jsonToConfig(json:String)= allCatch.either(read[ScreenConfig](json))
  def createFromJsonWithParser(parser:String=>ScreenConfig)(json:String):Vnelt[ScreenBuilder] = Try(parseJsonToConfig(parser)(json)) |> tryToVnel flatMap configToBuilder
  def parseJsonToConfig(parser:String=>ScreenConfig)(jsonStr:String):ScreenConfig = jsonStr |> parser

  private def readFile(path: String)= allCatch.either(Source.fromFile(path).getLines().mkString("\n"))
  private def classToConstructor(clazz: Class[_]) = allCatch.either {
    if (!classOf[Screen].isAssignableFrom(clazz)) throw new RuntimeException("specified class is not a subclass of Screen! : " + clazz.getCanonicalName)
    val constructor = clazz.getConstructor(classOf[AssetManager])
    (am: AssetManager) => constructor.newInstance(am).asInstanceOf[Screen]
  }


}
case class ScreenConfig(screenClass: Class[_], assets: Assets)
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
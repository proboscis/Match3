package com.glyph._scala.lib.libgdx.screen

import com.badlogic.gdx.assets.AssetManager
import scalaz.Scalaz._
import scalaz._
import scala.util.control.Exception._
import scala.io.Source
import com.badlogic.gdx.Screen
import com.glyph._scala.lib.libgdx.screen.ScreenBuilder.{Assets, Vnelt}
import scala.util.Try
import spray.json.{JsValue, RootJsonFormat}
import spray.json._
import DefaultJsonProtocol._
import com.glyph._scala.lib.libgdx.Builder
import scala.language.implicitConversions
/**
 * @author glyph
 */
trait ScreenBuilder extends Builder[Screen]{
  def requirements: Set[(Class[_], Seq[String])]
  def create(implicit assetManager: AssetManager): Screen
}
case class ScreenConfig(screenClass: Class[_], assets: Set[(Class[_],Seq[String])])
object ScreenBuilder {
  type Vnelt[T] = ValidationNel[Throwable, T]
  type Assets = Set[(Class[_], Seq[String])]

  implicit object ClassFormat extends  RootJsonFormat[Class[_]] {
    def write(obj: Class[_]): JsValue = JsString(obj.getCanonicalName)
    def read(json: JsValue): Class[_] = json match {
      case JsString(str) => Class.forName(str)
      case a => deserializationError("class name expected, but found " + a)
    }
  }
  implicit val confFormat = jsonFormat[Class[_],Assets,ScreenConfig](ScreenConfig,"screenClass","assets")

  private implicit def eitherToVnelt[T](either: Either[Throwable, T]): Validation[NonEmptyList[Throwable], T] = either fold(_.failNel, _.success)
  private implicit def tryToVnel[T](t:Try[T]):ValidationNel[Throwable,T] = t match{
    case scala.util.Success(s) => s.successNel
    case scala.util.Failure(f) => f.failNel
  }
  def apply(assets:Assets)(constructor:AssetManager => Screen):ScreenBuilder =new ScreenBuilder{
    def requirements: Assets = assets
    def create(implicit assetManager: AssetManager): Screen = constructor(assetManager)
  }
  def configToBuilder(config:ScreenConfig)= config.screenClass |> classToConstructor map ScreenBuilder(config.assets)
  def writeConfig(config: ScreenConfig) = config.toJson.prettyPrint
  def createFromFile(filePath: String) = filePath |> readFile flatMap createFromJson
  def createFromJson(json:String) = json |> jsonToConfig flatMap configToBuilder
  def jsonToConfig(json:String)= allCatch.either(json.asJson.convertTo[ScreenConfig])
  def createFromJsonWithParser(parser:String=>ScreenConfig)(json:String):Vnelt[ScreenBuilder] = Try(parseJsonToConfig(parser)(json)) |> tryToVnel flatMap configToBuilder
  def parseJsonToConfig(parser:String=>ScreenConfig)(jsonStr:String):ScreenConfig = jsonStr |> parser

  private def readFile(path: String)= allCatch.either(Source.fromFile(path).getLines().mkString("\n"))
  private def classToConstructor(clazz: Class[_]) = allCatch.either {
    if (!classOf[Screen].isAssignableFrom(clazz)) throw new RuntimeException("specified class is not a subclass of Screen! : " + clazz.getCanonicalName)
    val constructor = clazz.getConstructor(classOf[AssetManager])
    (am: AssetManager) => constructor.newInstance(am).asInstanceOf[Screen]
  }/*
  implicit def clazzJSONR:JSONR[Class[_]] = ???
  implicit def confJSONR: JSONR[ScreenConfig] = ScreenConfig.applyJSON(field[String]("screenClass"), field[Set[(Class[_],Array[String])]]("assets"))
  def createWithScalaz = parse(_:String) |> {
    case json => (field[Class[_]]("screenClass")(json) |@| field[Set[(Class[_],Array[String])]]("assets")(json)){ScreenConfig}
  }
  */
}

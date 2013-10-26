package com.glyph.scala.lib.libgdx.screen

import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.json.JSON
import scalaz.Scalaz._
import scalaz._
import scala.util.control.Exception._
import scala.io.Source
import com.badlogic.gdx.Screen
import com.codahale.jerkson.Json._
import com.glyph.scala.lib.libgdx.screen.ScreenConfig

/**
 * @author glyph
 */
trait ScreenBuilder {
  type FileName = String

  def requiredAssets: Map[Class[_], Array[FileName]]

  def create(assetManager: AssetManager): Screen
}
case class ScreenConfig(screenClass: Class[_], assets: Map[Class[_], Array[String]])
object ScreenBuilder {
  type Vnelt[T] = ValidationNel[Throwable, T]

  def createBuilder(filePath: String): Vnelt[ScreenBuilder] = {
    for {
      json <- (filePath |> readFile).map(src => JSON(src))
      className <- json.screenClass.asVnel[String]
      screenClass <- className |> strToClass
      assets <- json.assets |> json2Resources
      constructor <- screenClass |> classToConstructor
    } yield new ScreenBuilder {
      def requiredAssets: Map[Class[_], Array[FileName]] = assets

      def create(assetManager: AssetManager): Screen = constructor(assetManager)
    }
  }

  import com.codahale.jerkson.Json._

  def createFromJson(filePath: String): Vnelt[ScreenBuilder] = readFile(filePath).flatMap {
    jsonString => allCatch.either {
      println(jsonString)
      parse[ScreenConfig](jsonString)
    } |> eitherToVnelt flatMap {
      case ScreenConfig(screenClass, assets) => (for {
        constructor <- classToConstructor(screenClass)
      } yield (constructor, assets)) map {
        case (constructor, assets) => new ScreenBuilder {
          def create(assetManager: AssetManager): Screen = constructor(assetManager)

          def requiredAssets: Map[Class[_], Array[this.type#FileName]] = assets
        }
      }
    }
  }


  private def v2o[T](vnel: Vnelt[T]): Option[T] = vnel.fold(
    errors => {
      for (e <- errors) {
        e.printStackTrace()
      }
      none
    }, _.some)

  private def json2Resources(json: JSON): Vnelt[Map[Class[_], Array[String]]] = json.asMap.flatMap {
    map =>
      def vsjSetToVcsSet(set: Set[(String, JSON)]): Vnelt[Set[(Class[_], Array[String])]] = {
        set.map {
          case (str, json) => for (cls <- strToClass(str); ary <- json.toArray[String]) yield (cls, ary)
        }.foldLeft(Set.empty[(Class[_], Array[String])].successNel[Throwable]) {
          (vnelList, vnelSet) => for (list <- vnelList; s <- vnelSet) yield list + s
        }
      }
      //.sequence[({type l[a] = ValidationNel[Throwable,a]})#l, (Class[_], Seq[String])].map{_.toSet}
      map.toSet |> vsjSetToVcsSet map (_.toMap)
  }


  private def strToClass(str: String): Vnelt[Class[_]] = allCatch.either {
    "strToClass:" + str |> println
    Class.forName(str)
  }

  private def readFile(path: String): Vnelt[String] = allCatch.either {
    Source.fromFile(path).getLines().mkString("\n")
  }

  private def classToConstructor(clazz: Class[_]): Vnelt[AssetManager => Screen] = allCatch.either {
    if (!classOf[Screen].isAssignableFrom(clazz)) throw new RuntimeException("specified class is not a subclass of Screen! : " + clazz.getCanonicalName)
    val constructor = clazz.getConstructor(classOf[AssetManager])
    (am: AssetManager) => constructor.newInstance(am).asInstanceOf[Screen]
  }

  private def classToInstance(am: AssetManager)(clazz: Class[_]): Vnelt[Screen] = allCatch.either {
    clazz.getConstructor(classOf[AssetManager]).newInstance(am).asInstanceOf[Screen]
  }

  private implicit def eitherToVnelt[T](either: Either[Throwable, T]): Validation[NonEmptyList[Throwable], T] = either fold(_.failNel, _.success)
}
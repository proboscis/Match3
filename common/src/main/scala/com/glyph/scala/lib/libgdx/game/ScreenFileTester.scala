package com.glyph.scala.lib.libgdx.game

import com.badlogic.gdx.Gdx
import scalaz._
import Scalaz._
import scala.util.Try
import com.glyph.scala.lib.libgdx.screen.{ScreenConfig, ScreenBuilder}
import com.glyph.scala.game.action_puzzle.screen.ActionScreen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * @author glyph
 */
class ScreenFileTester(filename: String) extends ScreenBuilderSupport {
  override def create() {
    super.create()
    import ScreenBuilder._

    val actionScreenConfig = ScreenConfig(classOf[ActionScreen], Set(classOf[Texture] -> Array(
      "data/dummy.png",
      "data/particle.png",
      "data/sword.png",
      "data/round_rect.png"),
      classOf[Skin] -> Array("skin/default.json")))
    println(ScreenBuilder.writeConfig(actionScreenConfig))
/*
    val jsonAst = actionScreenConfig.toJson
    println(jsonAst)
    val obj = jsonAst.convertTo[ScreenConfig]
*/
    Try(Gdx.files.internal(filename).readString()) |> tryToVnel2 flatMap createFromJson match {
      case Success(s) => setBuilder(s)
      case Failure(e) => e foreach (_.printStackTrace()); Gdx.app.exit()
    }
  }

  def tryToVnel2[T](t: Try[T]): ValidationNel[Throwable, T] = t match {
    case scala.util.Success(s) => s.successNel
    case scala.util.Failure(f) => f.failNel
  }
}

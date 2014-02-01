package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.glyph.scala.lib.libgdx.actor.table.{BuilderSupport, StackActor, ActorHolder}
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Label}
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.glyph.scala.game.action_puzzle.view.{Title, TitleBuilder}
import com.glyph.scala.game.builders.Builders

/**
 * @author glyph
 */
class ActorHolderTest extends ScreenBuilder {
  def requirements: Set[(Class[_], Seq[String])] =
    Set(classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json"))

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
    debug() = true
    val skin = "skin/holo/Holo-dark-xhdpi.json".fromAssets[Skin]
    val holder = new ActorHolder with StackActor with BuilderSupport
    val loadingLabel = new Label("Loading", skin)
    holder.setFromBuilder(Builders.title("actorHolderTest",()=>{}))(loadingLabel)(f => loadingLabel.setText("%.0f".format(f)))
    root.add(holder).fill.expand()
  }
}
//TODO アニメーションで画面を切り替えるにはテーブルの切り替えではなくデータ構造の切り替えをしなければならない
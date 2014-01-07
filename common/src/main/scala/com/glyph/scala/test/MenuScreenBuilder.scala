package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.{ScrollPane, TextButton, List, Skin}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.glyph.scala.test.TestClass._
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.{List => GdxList, TextButton, ScrollPane, Skin}
import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.Builder

class MenuScreenBuilder extends Builder[MenuScreen] {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[TextureAtlas] -> ("skin/default.atlas" :: Nil),
    classOf[Skin] -> ("skin/holo/Holo-dark-xhdpi.json"::
      "skin/holo/Holo-light-xhdpi.json" :: Nil)
  )
  def create(implicit assets: AssetManager) = new MenuScreen
}
class MenuScreen(implicit assets:AssetManager) extends ConfiguredScreen{
  var onLaunch = (cls:Class[_])=>{}
  debug() = false
  backgroundColor = Color.WHITE
  val skin = assets.get[Skin]("skin/holo/Holo-light-xhdpi.json")
  val tbStyle = skin.get("default", classOf[TextButtonStyle])
  val list = classNameSet.map {
    case (c, name) => name
  }.toArray[Object] |> (new GdxList(_, skin))
  val button = new TextButton("launch", skin)
  button.addListener(new ChangeListener {
    def changed(p1: ChangeEvent, p2: Actor) {
      classNameSet(list.getSelectedIndex)._1 |> onLaunch
    }
  })
  val scrolling = new ScrollPane(list, skin)
  scrolling.setScrollingDisabled(false, false)
  root.add(scrolling).fill.expand(1, 9).row
  root.add(button).fill.expand(1, 1)
}
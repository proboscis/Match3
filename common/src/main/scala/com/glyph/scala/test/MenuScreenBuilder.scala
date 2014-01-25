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

class MenuScreen[E](skin:Skin,elements:Seq[(String,E)],onLaunch:E=>Unit = (e:E)=>{}) extends ConfiguredScreen{
  debug() = false
  backgroundColor = Color.WHITE
  val list = elements.map {
    case (name,c) =>name
  }.toArray[Object] |> (new GdxList(_, skin))
  val button = new TextButton("launch", skin)
  button.addListener(new ChangeListener {
    def changed(p1: ChangeEvent, p2: Actor) {
      elements(list.getSelectedIndex)._2 |> onLaunch
    }
  })
  val scrolling = new ScrollPane(list, skin)
  scrolling.setScrollingDisabled(false, false)
  root.add(scrolling).fill.expand(1, 9).row
  root.add(button).fill.expand(1, 1)
}
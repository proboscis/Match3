package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.{List => GdxList, _}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.glyph._scala.test.TestClass._
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import scalaz._
import Scalaz._
import com.glyph._scala.lib.libgdx.Builder

class MenuScreen[E](skin:Skin,elements:Seq[(String,E)],onLaunch:E=>Unit = (e:E)=>{}) extends ConfiguredScreen{
  root.add(new MenuTable(skin,elements.toMap,onLaunch)).fill.expand
}

class MenuTable[E](skin:Skin,elements:Map[String,E],onConfirm:E=>Unit) extends Table{
  val list = new GdxList[String](skin)
  list.setItems(elements.keys.toSeq.sorted:_*)
  val button = new TextButton("confirm",skin)
  button.addListener(new ChangeListener {
    def changed(p1: ChangeEvent, p2: Actor) {
      elements(list.getSelection.getLastSelected) |> onConfirm
    }
  })
  val scrolling = new ScrollPane(list, skin)
  scrolling.setScrollingDisabled(false, false)
  add(scrolling).fill.expand(1, 9).row
  add(button).fill.expand(1, 1)
}

class StringSelector(skin:Skin,elements:Traversable[String],onConfirm:String=>Unit) extends Table{
  val list = new GdxList[String](skin)
  list.setItems(elements.toSeq.sorted:_*)
  val button = new TextButton("confirm",skin)
  button.addListener(new ChangeListener {
    def changed(p1: ChangeEvent, p2: Actor) {
      list.getSelection.getLastSelected |> onConfirm
    }
  })
  val scrolling = new ScrollPane(list, skin)
  scrolling.setScrollingDisabled(false, false)
  add(scrolling).fill.expand(1, 9).row
  add(button).fill.expand(1, 1)
}
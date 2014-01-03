package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.glyph.scala.lib.libgdx.actor.table.{StackActor, ActorHolder}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Label}
import com.glyph.scala.game.Glyphs
import Glyphs._
/**
 * @author glyph
 */
class ActorHolderTest extends ScreenBuilder {
  def requiredAssets: Set[(Class[_], Seq[String])] =
    Set(classOf[Skin]->Seq("skin/holo/Holo-dark-xhdpi.json"))

  def create(assetManager: AssetManager): Screen = new ConfiguredScreen {
    debug() = true
    implicit val assets = assetManager
    val skin = "skin/holo/Holo-dark-xhdpi.json".fromAssets[Skin]
    val holder = new ActorHolder with StackActor
    root.add(holder).fill.expand()
    type :=>[A,B] = PartialFunction[A,B]
    stage.setKeyboardFocus(root)
    root.addListener(new InputListener{
      override def keyDown(event: InputEvent, keycode: Int): Boolean = {
        super.keyDown(event, keycode)
        import Keys._
        val f: Int:=>Unit ={
          case A => {
            val label = new Label(""+holder.actorStack.size,skin)
            //root.add(label)
            //root.invalidate()
            holder.set(label)
          }
          case S => holder.pop()
        }
        val result = f.lift(keycode).isDefined
        log(result)
        result
      }
    })
  }
}

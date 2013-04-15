package com.glyph.scala.game.ui

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import com.glyph.scala.lib.math.Vec2

/**
 * @author glyph
 */
class UIButton(drawable: Drawable) extends Button(drawable) {

  var onReleased = (pos: Vec2) => {}
  var onPressing = () => {}
  var onPressed = (pos: Vec2) => true

  val inputListener = new InputListener() {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      super.touchDown(event, x, y, pointer, button)
      onPressed(Vec2.tmp.set(x, y))
      true
    }

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
      super.touchUp(event, x, y, pointer, button)
      onReleased(Vec2.tmp.set(x, y))
    }
  }

  addListener(inputListener)

  override def act(delta: Float) {
    super.act(delta)
    if (this.isPressed) {
      onPressing()
    }
  }
}

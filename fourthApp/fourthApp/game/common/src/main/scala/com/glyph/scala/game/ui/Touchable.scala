package com.glyph.scala.game.ui

import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Actor}
import com.glyph.scala.lib.math.Vec2

/**
 * @author glyph
 */
trait Touchable extends Actor{
  var onReleased = (pos: Vec2) => {}
  var onPressing = () => {}
  var onPressed = (pos: Vec2) => true

  val inputListener = new InputListener() {
    private var pressed = false
    def isPressed = pressed
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      super.touchDown(event, x, y, pointer, button)
      onPressed(Vec2.tmp.set(x, y))
      pressed = true
      true
    }

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
      super.touchUp(event, x, y, pointer, button)
      onReleased(Vec2.tmp.set(x, y))
      pressed = false
    }
  }

  addListener(inputListener)

  override def act(delta: Float) {
    super.act(delta)
    if (inputListener.isPressed) {
      onPressing()
    }
  }
}

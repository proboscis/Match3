package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Actor}
import com.glyph.scala.lib.math.Vec2
import com.badlogic.gdx.math.Vector2

/**
 * @author glyph
 */
trait Touchable extends Actor {
  var onReleased = (x:Float,y:Float) => {}
  var onPressing = () => {}
  var onPressed = (x:Float,y:Float) => {}
  var onDrag = (x:Float,y:Float) => {}
  setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled)
  val inputListener = new InputListener() {
    private var pressed = false
    def isPressed = pressed
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      super.touchDown(event, x, y, pointer, button)
      onPressed(x,y)
      pressed = true
      true
    }


    override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
      super.touchDragged(event, x, y, pointer)
      onDrag(x,y)
    }

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
      super.touchUp(event, x, y, pointer, button)
      onReleased(x,y)
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

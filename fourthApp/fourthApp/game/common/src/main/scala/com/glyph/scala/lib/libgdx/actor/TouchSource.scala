package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Actor, Touchable}
import com.glyph.scala.lib.util.reactive.EventSource
import com.badlogic.gdx.utils.Disposable

/**
 * if you do not clear the input listeners when you remove this.
 * causes a memory leak.
 * @author glyph
 */
trait TouchSource extends Actor with Disposable{
  type POS = (Float, Float)
  val press = new EventSource[POS]
  val drag = new EventSource[POS]
  val release = new EventSource[POS]
  setTouchable(Touchable.enabled)
  addListener(new InputListener() {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      press.emit(x, y)
      event.stop()
      true
    }

    override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
      super.touchDragged(event, x, y, pointer)
      drag.emit(x, y)
    }

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
      super.touchUp(event, x, y, pointer, button)
      release.emit(x, y)
    }
  })

  def dispose() {
    //println("disposed")
    press.dispose()
    drag.dispose()
    release.dispose()
  }
}

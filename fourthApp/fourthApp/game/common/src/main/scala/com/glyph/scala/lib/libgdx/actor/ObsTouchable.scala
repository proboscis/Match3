package com.glyph.scala.lib.libgdx.actor

import com.glyph.scala.lib.util.observer.Observable
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Touchable, Actor}


/**
 * @author glyph
 */
trait ObsTouchable extends Actor{
  val press = new Observable[(Float,Float)]
  val drag = new Observable[(Float,Float)]
  val release = new Observable[(Float,Float)]
  setTouchable(Touchable.enabled)
  addListener(new InputListener(){
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      //super.touchDown(event,x,y,pointer,button)
      press(x,y)
      event.stop()//stop event !
      true
      //false
    }

    override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
      super.touchDragged(event, x, y, pointer)
      drag(x,y)
    }

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
      super.touchUp(event, x, y, pointer, button)
      release(x,y)
    }
  })
}

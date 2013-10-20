package com.glyph.scala.lib.libgdx.actor.action

import com.glyph.scala.lib.util.callback.Callback
import com.badlogic.gdx.scenes.scene2d.Action

/**
 * @author glyph
 */
class Finish extends Action {
  val onFinish = new Callback
  def act(p1: Float): Boolean = {onFinish();true}
}
object Finish{
  def apply(f: => Unit):Finish ={
    val fin = new Finish
    fin.onFinish(f)
    fin
  }
}

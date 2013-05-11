package com.glyph.scala.lib.util.screen

import com.glyph.libgdx.asset.AM
import com.glyph.scala.lib.util.callback.Callback

/**
 * @author glyph
 */
trait Loader extends Screen{
  val onFinish = new Callback
  override def render(delta: Float) {
    super.render(delta)
    val done = AM.instance().update()
    println(AM.instance().getProgress)
    if (done){
      onFinish()
    }
  }
}

package com.glyph.scala.lib.libgdx.screen

import com.glyph.java.asset.AM
import com.glyph.scala.lib.util.callback.DeprecatedCallback
import com.glyph.scala.lib.util.screen.Screen

/**
 * @author glyph
 */
trait Loader extends Screen{
  val onFinish = new DeprecatedCallback
  override def render(delta: Float) {
    super.render(delta)
    val done = AM.instance().update()
    //println(AM.instance().getProgress)
    if (done){
      onFinish()
    }
  }
}
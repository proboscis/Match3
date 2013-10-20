package com.glyph.scala.lib.libgdx.screen

import com.glyph.java.asset.AM
import com.glyph.scala.lib.util.callback.DeprecatedCallback
import com.glyph.scala.lib.util.screen.Screen

/**
 * @author glyph
 */
class Loader(onFinish: =>Unit) extends Screen{
  override def render(delta: Float) {
    super.render(delta)
    val done = AM.instance().update()
    //println(AM.instance().getProgress)
    if (done){
      onFinish
    }
  }
}

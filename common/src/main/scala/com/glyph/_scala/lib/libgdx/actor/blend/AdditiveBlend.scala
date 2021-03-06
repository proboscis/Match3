package com.glyph._scala.lib.libgdx.actor.blend

import com.badlogic.gdx.graphics.GL10

/**
 * @author glyph
 */
trait AdditiveBlend extends BlendFuncMod {
  val SRC_FUNC: Int = GL10.GL_SRC_ALPHA

  val DST_FUNC: Int = GL10.GL_ONE
}

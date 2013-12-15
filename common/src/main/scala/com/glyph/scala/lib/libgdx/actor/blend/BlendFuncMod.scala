package com.glyph.scala.lib.libgdx.actor.blend

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.lib.libgdx.actor.SpriteBatchRenderer

/**
 * @author proboscis
 */
trait BlendFuncMod extends SpriteBatchRenderer {
  val SRC_FUNC: Int
  val DST_FUNC: Int
  override def drawChildren(batch: SpriteBatch, parentAlpha: Float){
    val src = batch.getBlendSrcFunc
    val dst = batch.getBlendDstFunc
    batch.setBlendFunction(SRC_FUNC, DST_FUNC)
    super.drawChildren(batch, parentAlpha)
    batch.setBlendFunction(src, dst)
  }
}

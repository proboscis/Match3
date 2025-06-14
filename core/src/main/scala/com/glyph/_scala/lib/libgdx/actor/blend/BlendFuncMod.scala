package com.glyph._scala.lib.libgdx.actor.blend

import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch}
import com.glyph._scala.lib.libgdx.actor.SpriteBatchRenderer
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * @author proboscis
 */
trait BlendFuncMod extends Group{
  val SRC_FUNC: Int
  val DST_FUNC: Int
  override def drawChildren(batch: Batch, parentAlpha: Float){
    val src = batch.getBlendSrcFunc
    val dst = batch.getBlendDstFunc
    batch.setBlendFunction(SRC_FUNC, DST_FUNC)
    super.drawChildren(batch, parentAlpha)
    batch.setBlendFunction(src, dst)
  }
}

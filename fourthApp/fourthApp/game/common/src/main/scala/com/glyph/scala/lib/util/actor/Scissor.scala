package com.glyph.scala.lib.util.actor

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack

/**
 * @author glyph
 */

trait Scissor extends Group {
  private val clipBounds = new Rectangle()
  private val scissors = new Rectangle()

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    batch.flush()
    clipBounds.set(getX,getY,getWidth,getHeight)
    ScissorStack.calculateScissors(getStage.getCamera,batch.getTransformMatrix,clipBounds,scissors)
    ScissorStack.pushScissors(scissors)
    super.draw(batch, parentAlpha)
    batch.flush()
    ScissorStack.popScissors()

  }
}

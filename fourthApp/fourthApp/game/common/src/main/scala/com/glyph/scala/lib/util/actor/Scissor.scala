package com.glyph.scala.lib.util.actor

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.glyph.libgdx.gl.GLUtil

/**
 * @author glyph
 */

trait Scissor extends Group {
  private val pos1 = new Vector3
  private val pos2 = new Vector3

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    batch.flush()
    pos1.set(getX,getY,0)
    pos2.set(getX + getWidth, getY + getHeight,0)
    val camera = getStage().getCamera
    // calc display position
    camera.project(pos1);
    camera.project(pos2);
    val vx = Math.round(pos1.x);
    val vy = Math.round(pos1.y);
    val vw = Math.round(pos2.x - pos1.x);
    val vh = Math.round(pos2.y - pos1.y);

    GLUtil.instance().save()
    GLUtil.instance().glScissor(vx,vy,vw,vh)
    GLUtil.instance().setScissor(true)
    super.draw(batch, parentAlpha)
    batch.flush()
    GLUtil.instance().restore()
  }
}

package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * @author glyph
 */
trait Offset extends Actor{
  private var oldMatrix:Matrix4 = null
  var offset:Matrix4 = new Matrix4
  //TODO やはりオフセットは他のアクターで
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    oldMatrix = batch.getTransformMatrix
    super.draw(batch, parentAlpha)
  }
}

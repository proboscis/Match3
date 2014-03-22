package com.glyph._scala.lib.libgdx.drawable

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.graphics.g2d.Batch

/**
 * @author glyph
 */
class DrawableCopy(src:Drawable) extends Drawable{
  override def draw(batch:Batch,a:Float,b:Float,c:Float,d:Float) = src.draw(batch,a,b,c,d)

  override def getLeftWidth: Float = src.getLeftWidth
  override def setLeftWidth(width:Float) = src.setLeftWidth(width)

  override def getRightWidth: Float = src.getRightWidth
  override def setRightWidth(width:Float) = src.setRightWidth(width)

  override def getTopHeight: Float = src.getTopHeight
  override def setTopHeight(height:Float) = src.setTopHeight(height)

  override def getBottomHeight: Float = src.getBottomHeight
  override def setBottomHeight(height:Float)=src.setBottomHeight(height)

  override def getMinWidth: Float = src.getMinWidth
  override def setMinWidth(width:Float) = src.setMinWidth(width)

  override def getMinHeight: Float = src.getMinHeight
  override def setMinHeight(height:Float) = src.setMinHeight(height)
}

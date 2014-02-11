package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import com.badlogic.gdx.graphics.g2d.Batch

/**
 * @author glyph
 */
trait SameSize extends Group{
  override def setWidth(width: Float): Unit = {
    super.setWidth(width)
    val it = getChildren.iterator()
    while(it.hasNext){
      it.next().setWidth(width)
    }
  }

  override def setHeight(height: Float): Unit = {
    super.setHeight(height)
    val it = getChildren.iterator()
    while(it.hasNext){
      it.next().setHeight(height)
    }
  }

  override def setBounds(x: Float, y: Float, width: Float, height: Float): Unit = {
    super.setBounds(x, y, width, height)
    val it = getChildren.iterator()
    while(it.hasNext){
      val next = it.next()
      next.setWidth(width)
      next.setHeight(height)
    }
  }
}


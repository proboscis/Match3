package com.glyph.scala.lib.libgdx.actor.widgets

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.{Touchable, Actor}
import com.badlogic.gdx.math.Vector2

/**
 * @author glyph
 */
trait Layered extends WidgetGroup {

  setTouchable(Touchable.childrenOnly)
  override def layout() {
    super.layout()
    updateChildren()
  }
  private def updateChildren(){
    val children = getChildren
    val array = children.begin()
    var i = 0
    while (i < children.size) {
      //println("setSize of the layer=>" +(getX, getY, getWidth, getHeight))
      val child = array(i)
      child.setWidth(getWidth)
      child.setHeight(getHeight)
      i += 1
    }
    children.end()
  }

  override def setSize(width: Float, height: Float) {
    super.setSize(width, height)
    updateChildren()
  }

  override def setWidth(width: Float) {
    super.setWidth(width)
    updateChildren()
  }

  override def setHeight(height: Float) {
    super.setHeight(height)
    updateChildren()
  }
}

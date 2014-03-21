package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import com.badlogic.gdx.graphics.g2d.Batch

/**
 * @author glyph
 */
trait SameSize extends Group{

  override def sizeChanged(): Unit = {
    super.sizeChanged()
    val children = getChildren.begin()
    var i = 0
    val length = getChildren.size
    while(i < length){
      val child = children(i)
      child.setWidth(getWidth)
      child.setHeight(getHeight)
      i += 1
    }
    getChildren.end()
  }
}


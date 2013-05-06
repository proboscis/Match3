package com.glyph.scala.lib.util.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.util.LinkedList

/**
 * @author glyph
 */
trait Actable extends Actor{
  private val updates = new LinkedList[(Float)=>Unit]

  override def act(delta: Float) {
    super.act(delta)
    updates.foreach(_(delta))
  }

  def addActFunc(f:(Float)=>Unit){
    updates.push(f)
  }

  def removeActFunc(f:(Float)=>Unit){
    updates.remove(f)
  }
}

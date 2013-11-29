package com.glyph.scala.lib.libgdx.actor

import scala.collection.mutable.ListBuffer
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import com.badlogic.gdx.utils.SnapshotArray

/**
 * @author glyph
 */
trait SpriteRenderer extends Group{
  val sprites:SnapshotArray[Sprite] = new SnapshotArray[Sprite]()
  def addSprite(sprite:Sprite){
    sprites.add(sprite)
  }
  def removeSprite(sprite:Sprite,identity:Boolean = false){
    sprites.removeValue(sprite,identity)
  }

  override def drawChildren(batch: SpriteBatch, parentAlpha: Float){
    super.drawChildren(batch,parentAlpha)
    val snap:Array[_] = sprites.begin()
    val size = sprites.size
    var i = 0
    while(i<size){
      val s = snap(i).asInstanceOf[Sprite]
      s.draw(batch,getColor.a*parentAlpha)
      i += 1
    }
    sprites.end()
  }
}

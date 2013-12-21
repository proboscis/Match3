package com.glyph.scala.lib.libgdx.actor

import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch, Sprite}
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.SnapshotArray
import com.glyph.scala.lib.util.pool.{Pooling, Poolable, Pool}
import scala.reflect.ClassTag

/**
 * @author glyph
 */
trait SpriteRenderer extends Group {
  val sprites: SnapshotArray[Sprite] = new SnapshotArray[Sprite]()

  def addSprite(sprite: Sprite) {
    sprites.add(sprite)
  }

  def removeSprite(sprite: Sprite, identity: Boolean = true) {
    sprites.removeValue(sprite, identity)
  }

  override def drawChildren(batch: Batch, parentAlpha: Float) {
    super.drawChildren(batch, parentAlpha)
    val snap: Array[_] = sprites.begin()
    val size = sprites.size
    var i = 0
    while (i < size) {
      val s = snap(i).asInstanceOf[Sprite]
      s.draw(batch, getColor.a * parentAlpha)
      i += 1
    }
    sprites.end()
  }
}





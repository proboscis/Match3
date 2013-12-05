package com.glyph.scala.lib.libgdx.actor

import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import scala.reflect.ClassTag
import com.glyph.scala.lib.util.pool.Pool
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.SnapshotArray

/**
 * @author proboscis
 */
trait SpriteBatchRenderer extends Group {
  import Pool._
  val renderers  = new SnapshotArray[PooledRenderer[_]]()
  def addDrawable[T](tgt: T)(implicit evidence:SBDrawable[T],tag:ClassTag[T],rendererPool:Pool[PooledRenderer[T]]) {
    val renderer = auto[PooledRenderer[T]]
    renderer.setTarget(tgt)
    renderers.add(renderer)
  }

  def removeDrawable[T: SBDrawable](tgt: T) {
    var i = 0
    val array: Array[_] = renderers.begin()
    val size = renderers.size
    var found = false
    while (i < size && !found) {
      val renderer = array(i).asInstanceOf[PooledRenderer[_]]
      found |= renderer.target == tgt
      i += 1
    }
    renderers.end()
    if (found) {
      val renderer = renderers.get(i - 1)
      renderers.removeIndex(i - 1)
      renderer.freeToPool()
    }
  }

  override def drawChildren(batch: SpriteBatch, parentAlpha: Float) {
    super.drawChildren(batch, parentAlpha)
    val snap: Array[_] = renderers.begin()
    val size = renderers.size
    var i = 0
    while (i < size) {
      val s = snap(i).asInstanceOf[PooledRenderer[_]]
      s.draw(batch, parentAlpha)//TODO you might need to consider the alpha
      //s.draw(batch,getColor.a*parentAlpha)
      i += 1
    }
    renderers.end()
  }
}
object SpriteBatchRenderer {

  implicit object DrawableBuffer extends SBDrawable[ArrayBuffer[Sprite]] {
    def draw(tgt: ArrayBuffer[Sprite], batch: SpriteBatch, alpha: Float) {
      var i = 0
      val size = tgt.size
      while (i < size) {
        tgt(i).draw(batch, alpha)
        i += 1
      }
    }
  }

  implicit object DrawableSprite extends SBDrawable[Sprite] {
    def draw(tgt: Sprite, batch: SpriteBatch, alpha: Float) {
      tgt.draw(batch, alpha)
    }
  }
  var pools : ClassTag[_] Map Any = Map.empty.withDefault(_=>null)
  implicit def rendererPool[T](implicit ev:SBDrawable[T],tag:ClassTag[T],poolTag:ClassTag[PooledRenderer[T]]):Pool[PooledRenderer[T]] = {
    val result = pools(tag)
    if(result != null) result.asInstanceOf[Pool[PooledRenderer[T]]] else{
      import PooledRenderer._
      val created = Pool[PooledRenderer[T]](100)(genPoolingPooledRenderer[T],poolTag)
      pools +=(tag -> created)
      created
    }
  }
}


trait SBDrawable[T] {
  def draw(tgt: T, batch: SpriteBatch, alpha: Float)
}

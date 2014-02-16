package com.glyph._scala.lib.libgdx.actor

import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch, Sprite}
import scala.reflect.ClassTag
import com.glyph._scala.lib.util.pool.Pool
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.SnapshotArray
import com.glyph._scala.lib.util.Logging

/**
 * @author proboscis
 */
trait SpriteBatchRenderer extends Group with SBDrawableObject{
  import com.glyph._scala.game.Glyphs
  import Glyphs._
  import SpriteBatchRenderer._
  val renderers = new SnapshotArray[PooledRenderer[_]]()
  val objects = new com.badlogic.gdx.utils.Array[SBDrawableObject]()
  def addDrawable(tgt:SBDrawableObject){
    objects.add(tgt)
  }
  def addDrawable[T:SBDrawable:Class](tgt: T){
    val renderer = auto[PooledRenderer[T]]
    renderer.setTarget(tgt)
    renderers.add(renderer)
  }
  def removeDrawable(tgt:SBDrawableObject){
    objects.removeValue(tgt,true)
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

  override def drawChildren(batch: Batch, parentAlpha: Float) {
    super.drawChildren(batch, parentAlpha)
    val snap: Array[_] = renderers.begin()
    val size = renderers.size
    var i = 0
    while (i < size) {
      val s = snap(i).asInstanceOf[PooledRenderer[_]]
      s.draw(batch, parentAlpha) //TODO you might need to consider the alpha
      //s.draw(batch,getColor.a*parentAlpha)
      i += 1
    }
    renderers.end()
    val it = objects.iterator()
    while(it.hasNext)it.next().draw(batch,parentAlpha)
  }
}

object SpriteBatchRenderer extends Logging{
  var pools: Class[_] Map Any = Map.empty.withDefault(_ => null)
  implicit def rendererPool[T](implicit ev: SBDrawable[T], tag: Class[T], poolClass: Class[PooledRenderer[T]]): Pool[PooledRenderer[T]] = {
    val result = pools(tag)
    if (result != null) result.asInstanceOf[Pool[PooledRenderer[T]]]
    else {
      import PooledRenderer._
      val created = Pool[PooledRenderer[T]](100)(genPoolingPooledRenderer[T], poolClass)
      log("created a pool for : "+poolClass)
      pools += (tag -> created)
      created
    }
  }
}
object SBDrawableGdx extends SBDrawableGdxOps
trait SBDrawableGdxOps extends Logging{
  implicit object DrawableBuffer extends SBDrawable[ArrayBuffer[Sprite]] {
    def draw(tgt: ArrayBuffer[Sprite], batch: Batch, alpha: Float) {
      var i = 0
      val size = tgt.size
      while (i < size) {
        tgt(i).draw(batch, alpha)
        i += 1
      }
    }
  }

  implicit object DrawableSprite extends SBDrawable[Sprite] {
    def draw(tgt: Sprite, batch: Batch, alpha: Float) {
      tgt.draw(batch, alpha)
    }
  }
  class AnonSpriteRenderer extends ((Sprite)=>Unit){
    var batch:Batch = null
    var alpha:Float = 0f
    def set(b:Batch,a:Float):this.type = {
      batch = b
      alpha = a
      this
    }
    def apply(v1: Sprite): Unit = v1.draw(batch,alpha)
  }
  def drawableSpriteSeq[T<:Seq[Sprite]:ClassTag]:SBDrawable[T]= new SBDrawable[T] {
    log("created an evidence of SBDrawable for : "+implicitly[ClassTag[T]].runtimeClass)
    val f = new AnonSpriteRenderer
    def draw(tgt: T, batch: Batch, alpha: Float): Unit ={
      f.set(batch,alpha)
      tgt foreach f//this may be generating a new vector iterator!!
    }
  }
  private var tagToGeneratedDrawableMap:ClassTag[_] Map SBDrawable[_] = Map() withDefault(_=>null)
  implicit def spriteSeqToDrawable[T<:Seq[Sprite]:ClassTag]:SBDrawable[T] = {
    val tag = implicitly[ClassTag[T]]
    val result = tagToGeneratedDrawableMap(tag)
    if(result != null) result.asInstanceOf[SBDrawable[T]] else{
      val newOne = drawableSpriteSeq[T]
      tagToGeneratedDrawableMap += tag->newOne
      newOne
    }
  }
}

trait SBDrawable[T] {
  def draw(tgt: T, batch: Batch, alpha: Float)
}
trait SBDrawableObject{
  def draw(batch:Batch,alpha:Float):Unit
}
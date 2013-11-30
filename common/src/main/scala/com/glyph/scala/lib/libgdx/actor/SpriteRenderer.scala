package com.glyph.scala.lib.libgdx.actor

import scala.collection.mutable.ListBuffer
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import com.badlogic.gdx.utils.SnapshotArray
import com.glyph.scala.lib.util.pool.Pool

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
trait SBDrawable[T]{
  def draw(tgt:T,batch:SpriteBatch,alpha:Float)
}
object SpriteBatchRenderer{
  implicit object DrawableSpriteSeq extends SBDrawable[Seq[Sprite]]{
    def draw(tgt: Seq[Sprite], batch: SpriteBatch, alpha: Float){
      tgt foreach{
        s => s.draw(batch,alpha)
      }
    }
  }
}

trait SpriteBatchRenderer extends Group{
  import Pool._
  class ContextHolder(context:SBDrawable[_], target:_){
    def draw(batch:SpriteBatch,alpha:Float){
      context.draw(target,batch,alpha)
    }
  }
  implicit val contextHolderPool = Pool[ContextHolder]
  val sprites:SnapshotArray[ContextHolder] = new SnapshotArray()
  def addSprite[T](sprite:T)(implicit context:SBDrawable[T]){
    val holder = manual[ContextHolder]
    sprites.add(sprite)
  }
  def removeSprite[T](sprite:T,identity:Boolean = false){
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
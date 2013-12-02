package com.glyph.scala.lib.libgdx.actor

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import com.badlogic.gdx.utils.SnapshotArray
import com.glyph.scala.lib.util.pool.Pool
import scala.xml.Null
import scala.Null

/**
 * @author glyph
 */
trait SpriteRenderer extends Group{
  val sprites:SnapshotArray[Sprite] = new SnapshotArray[Sprite]()
  def addSprite(sprite:Sprite){
    sprites.add(sprite)
  }
  def removeSprite(sprite:Sprite,identity:Boolean = true){
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
  implicit object DrawableBuffer extends SBDrawable[ArrayBuffer[Sprite]]{
    def draw(tgt: ArrayBuffer[Sprite], batch: SpriteBatch, alpha: Float){
      var i = 0
      val size = tgt.size
      while (i < size){
        tgt(i).draw(batch,alpha)
        i += 1
      }
    }
  }
  implicit object DrawableSprite extends SBDrawable[Sprite]{
    def draw(tgt: Sprite, batch: SpriteBatch, alpha: Float){
      tgt.draw(batch,alpha)
    }
  }
}

trait SpriteBatchRenderer extends Group{
  import Pool._
  class ContextHolder(var target:Any,var drawer:(SpriteBatch,Float)=>Unit){
    def this() = this(null,null)
  }
  implicit val contextHolderPool = Pool[ContextHolder](()=>new ContextHolder,(holder:ContextHolder)=>{
    holder.target = null
    holder.drawer = null
  },1000)
  val sprites:SnapshotArray[ContextHolder] = new SnapshotArray()
  def addDrawable[T:SBDrawable](tgt:T){
    val holder = manual[ContextHolder]
    holder.target = tgt
    holder.drawer = (batch,alpha) =>{
      implicitly[SBDrawable[T]].draw(tgt,batch,alpha)
    }
    sprites.add(holder)
  }
  def removeDrawable[T:SBDrawable](tgt:T){
    var i = 0
    val array:Array[_] = sprites.begin()
    val size = sprites.size
    var found = false
    while(i < size && !found){
      val holder = array(i).asInstanceOf[ContextHolder]
      found |= holder.target == tgt
      i += 1
    }
    sprites.end()
    if(found){
      val holder = sprites.get(i-1)
      sprites.removeIndex(i-1)
      holder.free
    }
  }

  override def drawChildren(batch: SpriteBatch, parentAlpha: Float){
    super.drawChildren(batch,parentAlpha)
    val snap:Array[_] = sprites.begin()
    val size = sprites.size
    var i = 0
    while(i<size){
      val s = snap(i).asInstanceOf[ContextHolder]
      s.drawer(batch,parentAlpha)
      //s.draw(batch,getColor.a*parentAlpha)
      i += 1
    }
    sprites.end()
  }
}
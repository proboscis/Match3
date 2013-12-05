package com.glyph.scala.lib.libgdx.actor

import com.glyph.scala.lib.util.pool.{Poolable, Pooling}
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * @author proboscis
 */
class PooledRenderer[T:SBDrawable](var target:T) extends Poolable{
  def this() = this(null.asInstanceOf[T])
  val impl = implicitly[SBDrawable[T]]
  def setTarget(tgt:T){
    target = tgt
  }
  def reset(){
    target = null.asInstanceOf[T]
  }
  def draw(batch:SpriteBatch,alpha:Float){
    impl.draw(target,batch,alpha)
  }
}


object PooledRenderer{
  implicit def genPoolingPooledRenderer[T:SBDrawable] = new Pooling[PooledRenderer[T]]{
    def newInstance: PooledRenderer[T] = new PooledRenderer[T]()
    def reset(tgt: PooledRenderer[T]): Unit = tgt.reset()
  }
}

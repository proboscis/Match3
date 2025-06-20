package com.glyph._scala.lib.util.updatable.task

import com.glyph._scala.game.Glyphs
import com.glyph._scala.lib.util.pool.GlobalPool._
import com.glyph._scala.lib.util.pool.Poolable

/**
 * @author glyph
 */
class Do(var block: ()=>Unit) extends Task with AutoFree{
  def this() = this(()=>{})
  private var invoked = false
  def isCompleted: Boolean = invoked

  override def update(delta: Float) {
    super.update(delta)
    if(!invoked){
      block()
      invoked = true
      if(block.isInstanceOf[Poolable])block.asInstanceOf[Poolable].freeToPool()
    }
  }

  override def reset(): Unit = {
    super.reset()
    invoked = false
    block = null
  }
  def setCallback(f:()=>Unit):this.type ={
    block = f
    this
  }
}
object Do{
  import com.glyph._scala.lib.util.pool.GlobalPool._
  import Glyphs._
  def apply(f:()=>Unit):Do =  auto[Do].setCallback(f)
}
object Block{
  import com.glyph._scala.lib.util.pool.GlobalPool._
  import Glyphs._
  def apply(f: =>Unit):Do = auto[Do].setCallback(()=>f)
}
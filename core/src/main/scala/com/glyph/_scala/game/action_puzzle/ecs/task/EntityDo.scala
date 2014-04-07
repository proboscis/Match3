package com.glyph._scala.game.action_puzzle.ecs.task

import com.glyph._scala.lib.util.updatable.task.AutoFree
import com.glyph._scala.lib.ecs.Entity
import com.glyph._scala.lib.util.pool.{GlobalPool, Poolable}
import com.glyph._scala.game.Glyphs

/**
 * @author glyph
 */
class EntityDo(var entity:Entity,var f:Entity=>Unit) extends AutoFree {
  var invoked = false
  def setEntity(e:Entity):this.type ={
    entity = e
    this
  }
  override def isCompleted: Boolean = invoked

  override def update(delta: Float): Unit = {
    super.update(delta)
    if(!invoked){
      f(entity)
      invoked = true
    }
  }

  override def reset(): Unit = {
    super.reset()
    invoked = false
    entity = null
  }
}

class EntityFunction(var e:Entity,var f:Entity=>Unit) extends (()=>Unit) with Poolable{
  override def apply(): Unit = f(e)
  def this() = this(null,null)
  def reset(){
    e = null
    f = null
  }
}
object EntityFunction{
  import GlobalPool._
  import Glyphs._
  def apply(e:Entity,f:Entity=>Unit):EntityFunction = {
    val ef = auto[EntityFunction]
    ef.e = e
    ef.f = f
    ef
  }
}
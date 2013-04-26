package com.glyph.scala.lib.engine

import com.glyph.scala.lib.util.table.Table
import com.glyph.scala.lib.util.pool.AbstractPool

/**
 * @author glyph
 */
class EntityPackage(val name:String) extends AbstractPool[Entity]{
  val memberTable = new Table[Manifest[_],Any]
  val interfaceTable = new Table[Manifest[_],Any]
  protected def createNewInstance(): Entity = new Entity(this)
  
  def getMemberIndex[T:Manifest]:Int={
    memberTable.getIndex(implicitly[Manifest[T]])
  }
  def getInterfaceIndex[T<:Interface:Manifest]:Int={
    interfaceTable.getIndex(implicitly[Manifest[T]])
  }
}

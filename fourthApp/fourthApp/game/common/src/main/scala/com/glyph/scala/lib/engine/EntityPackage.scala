package com.glyph.scala.lib.engine

import com.glyph.scala.lib.util.table.Table
import com.glyph.scala.lib.util.pool.AbstractPool
import java.util
import com.glyph.scala.lib.event.EventManager

/**
 * @author glyph
 */
class EntityPackage(val name:String) extends AbstractPool[Entity]{
  val id = EntityPackage.nextId
  val memberTable = new Table[Manifest[_],Any]
  val interfaceTable = new Table[Manifest[_],Any]
  protected def createNewInstance(): Entity = new Entity(this)

  def getMemberIndex[T:Manifest]:Int={
    memberTable.getIndex(implicitly[Manifest[T]])
  }
  def getInterfaceIndex[T<:Interface:Manifest]:Int={
    interfaceTable.getIndex(implicitly[Manifest[T]])
  }

  def createInterfaceFilter(interests:Manifest[_<:Interface]*):java.util.BitSet={
    val result = new util.BitSet()
    interests.foreach {
      man =>result.set(getInterfaceIndex(man))
    }
    result
  }
  def createMemberFilter(interests:Manifest[_]*):util.BitSet={
    val result = new util.BitSet()
    interests.foreach {
      man =>result.set(getMemberIndex(man))
    }
    result
  }
}
object EntityPackage{
  private var currentId = 0
  def nextId :Int= {
    currentId += 1
    currentId -1
  }
}
package com.glyph.scala.lib.util.table

import com.glyph.java.util.ArrayBag
import java.util

/**
 * @author glyph
 */
class Row[K,V](table:Table[K,V]) {
  val elements = new ArrayBag[V]
  val elementBits = new util.BitSet()
  def get(key:K):V={
    elements.get(table.getIndex(key))
  }
  def set(key:K,value:V){
    set(table.getIndex(key),value)
  }
  def get(index:Int):V={
    elements.get(index)
  }
  def set(index:Int, value:V){
    elements.set(index,value)
    elementBits.set(index)
  }
  def remove(index:Int){
    set(index,null.asInstanceOf[V])
    elementBits.clear(index)
  }
  def remove(key:K){
    remove(table.getIndex(key))
  }
  def free(){
    clear()
    table.addFreed(this)
  }
  def clear(){
    elements.clear()
    elementBits.clear()
  }
}

package com.glyph.scala.lib.entity_property_system
import collection.mutable
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class Bag [+T](initialSize: Int)(implicit manifest:Manifest[T]){
  var array = new Array[T](initialSize)
  val nextQueue = mutable.Queue[Int]((0 to (array.size -1)):_*)
  def size = array.size
  def get(index:Int):T={
    if (index >= array.size){
      grow(index)
    }
    array(index)
  }
  def set(index:Int,e:T){
    if (index >= array.size){
      grow(index)
    }
    array(index) = e
  }

  def getEmptyIndex():Int={
    if(nextQueue.isEmpty){
      grow(array.size)
    }
    nextQueue.dequeue()
  }
  def grow(size:Int){
    while(size >= array.size){
      val prevSize = array.size
      array = array ++ new Array[T](array.size)
      nextQueue ++= prevSize to (array.size -1)
      Glyph.log("pool",array.size+"");
    }
  }
}

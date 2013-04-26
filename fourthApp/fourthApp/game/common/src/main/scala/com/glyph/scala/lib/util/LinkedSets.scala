package com.glyph.scala.lib.util

/**
 * @author glyph
 */
class LinkedSets[K,V] extends LinkedList[(K,V)]{
  def find(key:K):V={
    var result:V = null.asInstanceOf[V]
    val it = iterator
    var continue = it.hasNext()
    while(continue){
      val d = it.next()
      if (d._1 == key){
        result = d._2
        continue = false
      }else{
        continue = it.hasNext()
      }
    }
    result
  }
}

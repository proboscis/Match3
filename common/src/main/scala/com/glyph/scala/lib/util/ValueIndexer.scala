package com.glyph.scala.lib.util

/**
 * @author glyph
 */
class ValueIndexer[K,V]{
  val keyToIndex = new java.util.HashMap[K,(Int,V)]
  val indexer = new Indexer
  def getValue(key:K):V={
    val result = keyToIndex.get(key)
    if (result != null){
      result._2
    }else{
      null.asInstanceOf[V]
    }
  }
  def getIndex(key:K):Int ={
    val set :(Int,V)= keyToIndex.get(key)
    if (set == null){
      null.asInstanceOf[Int]
    }else{
      set._1
    }
  }
  def newIndex(key:K,value:V):Int ={
    val set = (indexer.getNext(),value)
    keyToIndex.put(key,set)
    set._1
  }
  def defined(key:K):Boolean = {
    keyToIndex.get(key) == null
  }
}

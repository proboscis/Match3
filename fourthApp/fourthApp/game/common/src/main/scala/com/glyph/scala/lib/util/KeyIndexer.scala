package com.glyph.scala.lib.util

/**
 * @author glyph
 */
class KeyIndexer [T]{
  val keyToIndex = new java.util.HashMap[T,Integer]
  val indexer = new Indexer
  def getIndex(key:T):Int ={
    var result = keyToIndex.get(key)
    if (result == null){
      result = indexer.getNext()
      keyToIndex.put(key,result)
    }
    result
  }
  def defined(key:T):Boolean ={
    keyToIndex.get(key) == null
  }
  def remove(key:T){
    val index = getIndex(key)
    keyToIndex.remove(key)
    indexer.addNext(index)
  }
}

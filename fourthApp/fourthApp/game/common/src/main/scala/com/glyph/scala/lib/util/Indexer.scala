package com.glyph.scala.lib.util



/**
 * @author glyph
 */
class Indexer(var size:Int){
  val indexQueue = new collection.mutable.Queue[Integer]
  def this(){
    this(0)
  }
  for(i <- 0 to size-1){
    indexQueue.enqueue(((size-1)-i))
  }
  def getNext():Int = {
    if (indexQueue.isEmpty){
      addNext(size)
      size += 1
    }
    indexQueue.dequeue()
  }
  def addNext(index:Int){
    indexQueue.enqueue(index)
  }
}
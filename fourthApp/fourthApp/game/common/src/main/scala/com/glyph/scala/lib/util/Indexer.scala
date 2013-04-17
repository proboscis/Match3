package com.glyph.scala.lib.util

/**
 * @author glyph
 */
class Indexer(var size:Int){
  val indexQueue = collection.mutable.Queue[Int]((0 to size -1):_*)
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
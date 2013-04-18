package com.glyph.scala.lib.util

import com.glyph.libgdx.util.ArrayStack

/**
 * @author glyph
 */
class Indexer(var size:Int){
  val indexQueue = new ArrayStack[Int]
  for(i <- 0 to size-1){
    indexQueue.push(((size-1)-i))
  }
  def getNext():Int = {
    if (indexQueue.isEmpty){
      addNext(size)
      size += 1
    }
    indexQueue.pop()
  }
  def addNext(index:Int){
    indexQueue.push(index)
  }
}
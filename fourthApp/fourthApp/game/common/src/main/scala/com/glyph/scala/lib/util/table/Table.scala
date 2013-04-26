package com.glyph.scala.lib.util.table

import com.glyph.scala.lib.util.pool.AbstractPool
import com.glyph.scala.lib.util.KeyIndexer

/**
 * @author glyph
 */
class Table[K, V] extends AbstractPool[Row[K, V]] {
  val indexer = new KeyIndexer[K]
  protected def createNewInstance(): Row[K, V] = new Row(this)
  def getIndex(key: K):Int ={
    indexer.getIndex(key)
  }
}

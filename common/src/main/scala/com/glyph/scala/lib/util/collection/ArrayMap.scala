package com.glyph.scala.lib.util.collection

import com.glyph.java.util.ArrayBag
import java.util
import com.glyph.scala.lib.util.KeyIndexer

/**
 * @author glyph
 */
class ArrayMap[K, V] {
  val values = new ArrayBag[V]
  val indexer = new KeyIndexer[K]
  val bits = new util.BitSet()

  def getIndex(key:K):Int={
    indexer.getIndex(key)
  }

  def set(key: K, value: V) {
    val index = indexer.getIndex(key)
    values.set(index, value)
    bits.set(index)
  }

  def get(key: K): V = {
    values.get(indexer.getIndex(key))
  }

  def set(key: Int, value: V) {
    values.set(key, value)
    bits.set(key)
  }

  def get(key: Int) {
    values.get(key)
  }
  def remove(key:K){
    val index = indexer.getIndex(key)
    indexer.remove(key)
    values.set(index,null.asInstanceOf[V])
  }

  def foreach(proc: V => Unit) {
    var i = bits.nextSetBit(0)
    while (i >= 0) {
      proc(values.get(i))
      i = bits.nextSetBit(i + 1)
    }
  }
}

object ArrayMap{
  def apply[K,V]():ArrayMap[K,V]={
    new ArrayMap[K,V]
  }
}

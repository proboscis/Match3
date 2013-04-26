package com.glyph.scala.lib.util.pool

import com.glyph.libgdx.util.ArrayStack

/**
 * @author glyph
 */
abstract trait AbstractPool[T] {
  val pool = new ArrayStack[T]
  def obtain():T={
    if (pool.isEmpty){
      createNewInstance()
    }else{
      pool.pop()
    }
  }

  def addFreed(e:T){
    pool.push(e)
  }
  protected def createNewInstance():T
}

package com.glyph.scala.lib.util

/**
 * @author glyph
 */
class Poolable[T](pool:Pool[Poolable[T]]){
  def free(){
    pool.add(this)
  }
}

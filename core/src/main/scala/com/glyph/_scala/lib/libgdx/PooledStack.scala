package com.glyph._scala.lib.libgdx

import com.glyph._scala.lib.util.pool.{PoolOps, Pooling}
import com.glyph.ClassMacro._
/**
 * @author glyph
 */
class PooledStack[T:Pooling]{
  import com.glyph._scala.lib.util.pool.GlobalPool._
  import PoolOps._
  private val stack = new collection.mutable.ArrayStack[T]()
  /**
   * @return pushed matrix
   */
  def push():T = {
    val p = manual[T]
    stack.push(p)
    p
  }

  /**
   * @return popped matrix
   */
  def pop(){
    val popped = stack.pop()
    popped.free
  }
  def head = stack.head
  def isEmpty = stack.isEmpty
}

package com.glyph.scala.lib.util
import java.lang.reflect.Constructor
import com.glyph.libgdx.util.ArrayStack

/**
 * @author glyph
 */
class Pool[T](val constructor:Constructor[T],params:Any*){
  val pool = new ArrayStack[T]()
  def obtain():T={
    if (pool.isEmpty){
      constructor.newInstance(this,params)
    }else{
      pool.pop()
    }
  }
  def add(p:T){
    pool.push(p)
  }
}

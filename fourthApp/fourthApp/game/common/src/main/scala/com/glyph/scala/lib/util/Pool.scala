package com.glyph.scala.lib.util

import com.glyph.libgdx.util.ArrayStack
import com.glyph.scala.lib.util.Poolable

/**
 * @author glyph
 */

class Pool [T<:Poolable](implicit typ: Manifest[T]){
  val poolables = new ArrayStack[T]
  var runtimeClass = typ.runtimeClass
  def obtain():T={
    if (poolables.isEmpty){
      //Glyph.log("Pool","new "+runtimeClass.getSimpleName)
      runtimeClass.newInstance().asInstanceOf[T]
    }else{
      poolables.pop()
    }
  }
  def free(e:T){
    //Glyph.log("Pool","free "+runtimeClass.getSimpleName)
    e.free()
    poolables.push(e)
  }

  def setRuntimeClass(clazz :Class[_]):Pool[T]={
    runtimeClass = clazz
    this
  }
}

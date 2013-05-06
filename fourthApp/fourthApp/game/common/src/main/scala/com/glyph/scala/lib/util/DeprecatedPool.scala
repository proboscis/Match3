package com.glyph.scala.lib.util

import com.glyph.libgdx.util.ArrayStack
import com.glyph.scala.lib.util.DeprecatedPoolable

/**
 * @author glyph
 */
@deprecated
class DeprecatedPool [T<:DeprecatedPoolable](implicit typ: Manifest[T]){
  val poolables = new ArrayStack[T]
  var runtimeClass = typ.runtimeClass
  protected def newInstance():T={
    runtimeClass.newInstance().asInstanceOf[T]
  }

  def obtain():T={
    if (poolables.isEmpty){
      //Glyph.log("Pool","new "+runtimeClass.getSimpleName)
      newInstance()
    }else{
      poolables.pop()
    }
  }
  def free(e:T){
    //Glyph.log("Pool","free "+runtimeClass.getSimpleName)
    e.free()
    poolables.push(e)
  }

  def setRuntimeClass(clazz :Class[_]):DeprecatedPool[T]={
    runtimeClass = clazz
    this
  }
}

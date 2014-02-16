package com.glyph._scala.lib.util.reactive

/**
 * @author glyph
 */
class FloatVar {
  var variable:Float =0f
  val listeners = new com.badlogic.gdx.utils.Array[Float=>Unit]()
  def apply():Float = variable
  def update(nv:Float){
    variable = nv
    notifyObservers()
  }
  def notifyObservers(){
    val local = variable
    val itr =listeners.iterator()
    while(itr.hasNext){
      itr.next()(local)
    }
  }
  def +=(f:Float=>Unit):Float=>Unit = {
    listeners add f
    f(variable)
    f
  }
  def -=(f:Float=>Unit){
    listeners removeValue(f,true)
  }
  def map(f:Float=>Float):FloatVar ={
    val mapped = new FloatVar
    this += {
      float => mapped()=f(float)
    }
    mapped
  }
}

object FloatVar{
  def apply(f:Float):FloatVar={
    val newOne = new FloatVar
    newOne()=f
    newOne
  }
}
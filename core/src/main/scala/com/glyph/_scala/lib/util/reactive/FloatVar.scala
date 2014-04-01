package com.glyph._scala.lib.util.reactive

import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
class FloatVar extends Logging{
  self =>
  private var variable:Float =0f
  val listeners = new com.badlogic.gdx.utils.SnapshotArray[Object]()
  def apply():Float = variable
  def update(nv:Float){
    variable = nv
    notifyObservers()
  }
  def notifyObservers(){
    val local = variable
    val elements = listeners.begin()
    val size = listeners.size
    var i = 0
    while(i < size){
      elements(i).asInstanceOf[Float=>Unit](local)
      i+=1
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
  def mapTo[R](f:Float=>R):Varying[R] = new Varying[R]{
    var value = f(self.variable)
    override def current: R = value
    self += (nv =>{
      value = f(nv)
      notifyObservers(value)
    })
  }
}

object FloatVar{
  def apply(f:Float):FloatVar={
    val newOne = new FloatVar
    newOne()=f
    newOne
  }
}
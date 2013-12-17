package com.glyph.scala.lib.util.updatable.task.tween

import com.glyph.scala.lib.util.updatable.task.{AutoFree, InterpolationTask}

/**
 * @author proboscis
 */

trait Accessor[T]{
  def size:Int
  def get(tgt:T,result:Array[Float])
  def set(tgt:T,values:Array[Float])
}
class Tween[T] extends InterpolationTask with AutoFree{
  var target:T = null.asInstanceOf[T]
  var accessor:Accessor[T] = null.asInstanceOf[Accessor[T]]
  var start:Array[Float] = Array()
  var end:Array[Float] = Array()
  var buffer:Array[Float] = Array()
  var arraySize = 0
  var size = 0
  def setTarget(target:T,accessor:Accessor[T]):this.type = {
    this.target = target
    this.accessor = accessor
    validateSize()
    this
  }
  def validateSize(){
    size = accessor.size
    if(arraySize < size){
      size = accessor.size
      start = new Array[Float](size)
      end = new Array[Float](size)
      buffer = new Array[Float](size)
    }
  }

  override def onStart(){
    super.onStart()
    accessor.get(target,buffer)
    buffer.copyToArray(start)
    log(buffer.toSeq)
  }

  def apply(alpha: Float){
    var i = 0
    while (i < size){
      val s = start(i)
      buffer(i) = s + (end(i)-s) * alpha
      i+=1
    }
    accessor.set(target,buffer)
  }
  def to(x:Float):this.type = {end(0) = x;this}
  def to(x:Float,y:Float):this.type={end(0) = x;end(1) = y;this}
  def to(x:Float,y:Float,z:Float):this.type={end(0) = x;end(1) = y;end(2) = z;this}
  def to(x:Float,y:Float,z:Float,w:Float):this.type = {end(0) = x;end(1) = y;end(2)=z;end(3)=w;this}
  def to(to:Float*):this.type ={to.copyToArray(end);this}
  override def reset(){
    super.reset()
    size = 0
    target = null.asInstanceOf[T]
    accessor = null.asInstanceOf[Accessor[T]]
  }
}
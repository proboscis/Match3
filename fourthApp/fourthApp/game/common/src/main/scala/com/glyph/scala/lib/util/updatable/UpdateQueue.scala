package com.glyph.scala.lib.util.updatable

import collection.mutable

/**
 * if you enqueue something here, it will be invoked after specified interval has passed
 * @author glyph
 */
class UpdateQueue(interval:Float) extends Updatable{
  var timer = interval
  private val queue = mutable.Queue[()=>Unit]()
  def enqueue(f: =>Unit){
    queue.enqueue(()=>f)
  }
  def clear(){queue.clear()}
  override def update(delta:Float){
    if (timer >= 0){
      timer -= delta
    }
    if (timer <= 0 && !queue.isEmpty){
      queue.dequeue()()
      timer = interval
    }
  }
}

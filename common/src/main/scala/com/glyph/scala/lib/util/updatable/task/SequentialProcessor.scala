package com.glyph.scala.lib.util.updatable.task

import scala.collection.mutable
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
trait SequentialProcessor extends TaskProcessor with Logging{
  val tasks = new com.badlogic.gdx.utils.Array[Task](true,4)
  var position = 0
  var current: Task = null

  override def update(delta: Float) {
    super.update(delta)
    if (current == null) {
      if (tasks.size != 0) {
        log(position)
        current = tasks.get(position)
        current.onStart()
        position += 1
      }
    }
    if (current != null) {
      if (!current.isCompleted) {
        current.update(delta)
        if (current.isCompleted) {
          current.onFinish()
          current = null
        }
      }else{
        current.onFinish()
        current = null
      }
    }
  }

  override def add(task: Task): TaskProcessor = {
    super.add(task)
    tasks add task
    this
  }

  def cancel(task: Task) {
    if(current == task){
      current = null
    }
    tasks.removeValue(task,true)
    task.onCancel()
  }

  def reset(){
    log("being reset")
    tasks.clear()
    position = 0
  }
}

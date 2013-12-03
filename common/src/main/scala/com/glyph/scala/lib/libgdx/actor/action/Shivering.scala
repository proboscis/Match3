package com.glyph.scala.lib.libgdx.actor.action

import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.actor.Tasking
import com.glyph.scala.lib.util.updatable.task.{TaskProcessor, Task, IntegratingFTask, FunctionTask}
import com.glyph.scala.lib.util.animator.{AnimatedFloat2, Swinger}
import com.glyph.scala.lib.util.{Threading, Logging}
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/**
 * @author glyph
 */
trait Shivering extends Tasking with Logging with Threading{
  private var started = false

  val shiver = new IntegratingFTask(){
    override def update(delta: Float){
      assert(started)//this means that this task is updated while this trait is not started
      super.update(delta)
    }
  }


  override def add(task: Task): TaskProcessor = {
    assert(started)
    super.add(task)
  }

  def startShivering[T:AnimatedFloat2](tgt:T) {
    if (!started) {
      log("start shivering")
      val impl = implicitly[AnimatedFloat2[T]]
      shiver.setUpdater(Swinger.update(100,impl.getX(tgt) ,impl.getY(tgt),tgt))
      started = true
      add(shiver)
    }
  }
  def stopShivering() {
    if(started){
      if(!shiver.isCompleted){
        shiver.finish()
        shiver.reset()//this causes nullPointerException
      }
      started = false
      log("stop shivering")
    }
  }
}

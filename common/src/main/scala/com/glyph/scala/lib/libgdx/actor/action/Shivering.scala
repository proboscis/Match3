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
  private var count = 0
  val shiver = new IntegratingFTask(){
    override def update(delta: Float){
      //TODO this is the problem
      log("update")// this is called even after the invocation of stopShivering
      assert(!isCompleted)
      assert(started)//this means that this task is updated while this trait is not started
      //thia is the problem!
      //this is not added, but called....
      super.update(delta)
    }
  }


  override def add(task: Task): TaskProcessor = {
    assert(started)
    assert(task == shiver)
    assert(!task.isCompleted)
    //you have to remove the task immediately in order to reuse that....
    super.add(task)
  }

  def startShivering[T:AnimatedFloat2](tgt:T) {
    if (!started) {
      count += 1
      log("start shivering"+count)
      val impl = implicitly[AnimatedFloat2[T]]
      shiver.setUpdater(Swinger.update(100,impl.getX(tgt) ,impl.getY(tgt),tgt))
      shiver.setFinalizer(()=>{
        shiver.reset()
      })
      shiver.setCanceller(()=>{
        log("cancel")
        shiver.reset()
      })
      started = true
      add(shiver)
    }
  }
  def stopShivering() {
    if(started){
      if(!shiver.isCompleted){
        log("finishing shivering")
        cancel(shiver)
      }
      started = false
      log("stop shivering")
    }
  }
}

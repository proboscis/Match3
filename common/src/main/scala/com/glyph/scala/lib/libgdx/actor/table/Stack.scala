package com.glyph.scala.lib.libgdx.actor.table

import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin, Table, WidgetGroup}
import com.badlogic.gdx.scenes.scene2d.Actor
import scala.collection.mutable
import scala.util.Try
import com.glyph.scala.lib.util.updatable.task.{Do, Sequence, Task}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.Tasking
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder.Assets
import com.badlogic.gdx.scenes.scene2d.utils.Layout
import com.glyph.scala.lib.libgdx.Builder

trait ActorHolder extends WidgetGroup {
  def setSizeOfChildren() {
    val it = getChildren.iterator()
    while (it.hasNext) {
      val child = it.next
      child.setPosition(0, 0)
      child.setSize(getWidth, getHeight)
      child match{
        case c:Layout => c.layout()
        case _=> // do nothing
      }
    }
  }

  override def layout(): Unit = {
    super.layout()
    setSizeOfChildren()
  }
}

/**
 * @author glyph
 */
trait StackActor extends ActorHolder {
  val actorStack = mutable.Stack[Actor]()

  def push(actor: Actor) {
    if (!actorStack.isEmpty) {
      actorStack.head.remove()
    }
    addActor(actor)
    invalidate()
    actorStack.push(actor)
  }

  def pop(): Option[Actor] = (for (prev <- Try(actorStack.pop())) yield {
    prev.remove()
    if (!actorStack.isEmpty) {
      addActor(actorStack.head)
      invalidate()
    }
    prev
  }).toOption

  /**
   * calls a remove() method of an actor on top if the stack is not empty.
   */
  def callRemoveOfTop(){
    if(!actorStack.isEmpty)actorStack.head.remove()
  }
}

trait Progressing {
  def progress: Float
}

/**
 * use this task to load the assets
 * @param assets
 * @param progress
 * @param am
 */
class AssetTask(assets:Assets)(progress:Float=>Unit)(implicit am:AssetManager)extends Task with AssetManagerOps{
  am.load(assets)
  protected var completed = am.isLoaded(assets)
  def isCompleted: Boolean = completed
  override def update(delta: Float): Unit = {
    super.update(delta)
    progress(am.getProgress)
    completed = am.update()
  }
}

trait TaskWaiter extends Tasking{
  def wait(task:Task)(f:()=>Unit){
    add(Sequence(task,Do(f)))
  }
}

trait TaskSupport extends StackActor with Tasking{
  def load(task:Task,view:Actor,callback:()=>Unit){
    if (task.isCompleted) {
      callback()
    } else {
      callRemoveOfTop()
      addActor(view)
      add(Sequence(task,Do{
        view.remove()
        callback()
      }))
    }
  }
}
trait BuilderSupport extends TaskSupport{
  def setFromBuilder(builder:Builder[Actor])(view:Actor)(progress:Float=>Unit)(implicit am:AssetManager){
    val loader = new AssetTask(builder.requirements)(progress)
    load(loader,view,()=>{
      push(builder.create)
    })
  }
}


trait AssetManagerOps{
  type Assets = Set[(Class[_], Seq[String])]
  import BuilderOpsModule._
  implicit def assetsManagerToOps(am:AssetManager):AssetManagerOpsImpl = new AssetManagerOpsImpl(am)
}
object BuilderOpsModule{
  class AssetManagerOpsImpl(val am:AssetManager) extends AnyVal{
    def load(assets:Assets) = for ((c, files) <- assets; file <- files) am.load(file, c)
    def isLoaded(assets:Assets) = assets.forall(_._2.forall(am.isLoaded))
  }
}
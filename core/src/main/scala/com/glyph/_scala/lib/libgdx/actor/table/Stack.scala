package com.glyph._scala.lib.libgdx.actor.table

import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin, Table, WidgetGroup}
import com.badlogic.gdx.scenes.scene2d.Actor
import scala.collection.mutable
import scala.util.Try
import com.glyph._scala.lib.util.updatable.task.{Block, Do, Sequence, Task}
import com.badlogic.gdx.assets.{AssetDescriptor, AssetManager}
import com.glyph._scala.lib.libgdx.actor.Tasking
import com.glyph._scala.lib.libgdx.screen.ScreenBuilder.Assets
import com.badlogic.gdx.scenes.scene2d.utils.Layout
import com.glyph._scala.lib.libgdx.Builder

trait Layers extends WidgetGroup {
  def setSizeOfChildren() {
    // this is called as nested ? how ?
    val array = getChildren
    val items = array.begin()
    var i = 0
    val L = array.size
    while(i < L){
      val child = items(i)
      child.setPosition(0, 0)
      child.setSize(getWidth, getHeight)
      child match{
        case c:Layout => c.layout()
        case _=> // do nothing
      }
      i += 1
    }
  }

  override def layout(): Unit = {
    super.layout()
    setSizeOfChildren()
  }

  override def addActor(actor: Actor): Unit = {
    super.addActor(actor)
    setSizeOfChildren()
  }
}

/**
 * @author glyph
 */
trait StackActor extends Layers {
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
class AssetTask(assets:Seq[AssetDescriptor[_]])(progress:Float=>Unit)(implicit am:AssetManager)extends Task with AssetManagerOps{
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
    addTask(Sequence(task,Do(f)))
  }
}

trait TaskSupport extends StackActor with Tasking{
  def load(task:Task,view:Actor,callback:()=>Unit){
    if (task.isCompleted) {
      callback()
    } else {
      callRemoveOfTop()
      addActor(view)
      addTask(Sequence(task,Block{
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
    def load(assets:Seq[AssetDescriptor[_]]) = assets foreach am.load
    def load(assets:Assets) = for ((c, files) <- assets; file <- files) am.load(file, c)
    def isLoaded(assets:Assets) = assets.forall(_._2.forall(am.isLoaded))
    def isLoaded(assets:Seq[AssetDescriptor[_]]) = assets forall(desc => am.isLoaded(desc.fileName,desc.`type`))
    def unload(assets:Seq[AssetDescriptor[_]]) = assets.foreach(desc => am.unload(desc.fileName))
    def unload(assets:Assets){
      for{
        (cls,files)<-assets
        file <- files if am.isLoaded(file)
      }{
        am.unload(file)
      }
    }
  }
}
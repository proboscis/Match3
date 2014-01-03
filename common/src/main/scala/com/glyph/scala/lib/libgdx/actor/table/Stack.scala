package com.glyph.scala.lib.libgdx.actor.table

import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin, Table, WidgetGroup}
import com.badlogic.gdx.scenes.scene2d.Actor
import scala.collection.mutable
import scala.util.Try
import com.glyph.scala.lib.util.updatable.task.Task
import com.badlogic.gdx.assets.AssetManager

trait ActorHolder extends WidgetGroup {
  def setSizeOfChildren() {
    val it = getChildren.iterator()
    while (it.hasNext) {
      val child = it.next
      child.setPosition(0, 0)
      child.setSize(getWidth, getHeight)
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

  def set(actor: Actor) {
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
}

trait Progressing extends Task {
  def progress: Float
}

class AssetLoader(tgt:AssetManager) extends Progressing {
  protected var completed = false
  def isCompleted: Boolean = completed
  def progress: Float = tgt.getProgress
  override def update(delta: Float): Unit = {
    super.update(delta)
    completed = tgt.update()
  }
}

trait ProcessWaiter{
}

class ProcessWaiterImpl(tgt: Progressing,skin:Skin) extends Table {
  val formatter = "%.0f".format(_: Float)
  val label = new Label(formatter(tgt.progress), skin)
  add(label).fill.expand

  override def act(delta: Float): Unit = {
    super.act(delta)
    label.setText(formatter(tgt.progress))
  }
}

trait BuilderSupport extends StackActor {
  def assetManager:AssetManager
  def setFromBuilder(builder: Builder[Actor]) {
  }
}

trait Builder[+T] {
  def requirements: Set[(Class[_], Seq[String])]
  def create: T
}

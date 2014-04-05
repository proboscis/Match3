package com.glyph._scala.lib.ecs

import com.badlogic.gdx.utils.{ObjectMap, DelayedRemovalArray}
import com.glyph._scala.lib.util.pool.{PoolOps, Poolable}
import com.glyph._scala.lib.ecs.script.Script
import org.w3c.dom.Entity
import com.glyph._scala.game.Glyphs

trait EntityEvent extends Poolable{
  def reset()
}
case class AddChild(var child:Entity) extends EntityEvent{
  def this() = this(null)
  override def reset():Unit = child = null
}
case class RemoveChild(var child:Entity) extends EntityEvent{
  def this() = this(null)
  override def reset():Unit = child = null
}
case class AddScript(var script:Script) extends EntityEvent{
  def this() = this(null)
  override def reset(): Unit = script = null
}
case class RemoveScript(var script:Script) extends EntityEvent{
  def this() = this(null)
  override def reset(): Unit = script = null
}
import Glyphs._
import com.glyph._scala.lib.util.pool.GlobalPool._
final class Entity extends Poolable {
  var parent :Entity = null
  var _scene :Scene = null
  private [ecs] def setScene(scene:Scene) = _scene = scene
  def scene: Scene = if(_scene == null) if(parent == null) null else parent.scene else _scene
  private val mComponents = new com.badlogic.gdx.utils.ArrayMap[Class[_],Any]()
  private val mScripts = new DelayedRemovalArray[Script]()
  private val mScriptMap = new ObjectMap[Class[_], Script]()
  private val mModificationListeners = new DelayedRemovalArray[EntityEvent=>Unit]()
  val children = new DelayedRemovalArray[Entity]()

  /**
   * the listener will be notified of the modification even before the listener is added.
   * @param listener
   */
  def addModificationListener(listener:EntityEvent=>Unit){
    mModificationListeners.add(listener)
    if(mScripts.size > 0){
      mScripts.begin()
      var i = 0
      val size = mScripts.size
      while(i<size){
        val e = manual[AddScript]
        e.script = mScripts.get(i)
        listener(e)
        e.free
        i += 1
      }
      mScripts.end()
    }
    if(children.size > 0){
      children.begin()
      var i = 0
      val size = children.size
      while(i < size){
        val e = manual[AddChild]
        e.child = children.get(i)
        listener(e)
        e.free
        i+=1
      }
      children.end()
    }
  }
  def removeModificationListener(listener:EntityEvent=>Unit){
    mModificationListeners.removeValue(listener,true)
  }
  def fireModificationEvent(e:EntityEvent){
    mModificationListeners.begin()
    val it = mModificationListeners.iterator()
    while(it.hasNext){
      it.next()(e)
    }
    mModificationListeners.end()
  }
  def +=(child: Entity) = {
    children.add(child)
    child.parent = this
    val event = manual[AddChild]
    event.child = child
    fireModificationEvent(event)
    event.free
  }

  def -=(child: Entity) = {
    children.removeValue(child, true)
    val event = manual[RemoveChild]
    event.child = child
    fireModificationEvent(event)
    event.free
    child.freeToPool()
  }

  def +=(script: Script) = {
    mScripts.add(script)
    mScriptMap.put(script.getClass, script)
    val event = manual[AddScript]
    event.script = script
    fireModificationEvent(event)
    event.free
  }

  def -=(script: Script) {
    mScripts.removeValue(script, true)
    mScriptMap.remove(script.getClass)
    val event = manual[RemoveScript]
    event.script = script
    fireModificationEvent(event)
    event.free
    script.freeToPool()
  }

  def getScript[T<:Script:Class]:T = mScriptMap.get(implicitly[Class[T]]).asInstanceOf[T]

  def updateScripts(delta:Float){
    mScripts.begin()
    val it = mScripts.iterator()
    while(it.hasNext){
      val script = it.next()
      if(!script.isInitialized)script.initialize(this)
      script.update(delta)
    }
    mScripts.end()
    children.begin()
    val it2 = children.iterator()
    while(it2.hasNext){
      it2.next().updateScripts(delta)
    }
    children.end()
  }

  def reset() {
    {//clear scripts
      mScripts.begin()
      val it = mScripts.iterator()
      while (it.hasNext) {
        it.next.freeToPool()
      }
      mScripts.end()
      mScripts.clear()
      mScriptMap.clear()
    }
    
    {//clear children
      children.begin()
      val it = children.iterator()
      while(it.hasNext){
        it.next().freeToPool()
      }
      children.end()
      children.clear()
    }
    mModificationListeners.clear()
    _scene = null
    parent = null
  }

  def remove() {
    scene -= this
  }
}
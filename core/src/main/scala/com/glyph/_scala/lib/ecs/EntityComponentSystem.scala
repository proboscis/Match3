package com.glyph._scala.lib.ecs

import com.badlogic.gdx.utils.{ObjectMap, DelayedRemovalArray}
import com.glyph._scala.lib.util.pool.{PoolOps, Poolable}
import com.glyph._scala.lib.ecs.script.Script
import org.w3c.dom.Entity
import com.glyph._scala.game.Glyphs
import com.glyph._scala.lib.util.DelayedArray

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

trait Component extends Poolable
trait IsComponent[T<:Poolable]{
  var scene:Scene = null
  var componentId = 0
  private def ensureComponentID(e:Entity){
    if(scene == null || scene.isDisposed){
      val eScene = e.scene
      if( eScene != scene) e.scene.setOrRegisterComponentId(this)
    }
  }
  def getComponent(e:Entity):T = {
    ensureComponentID(e)
    e.getComponentUnSafe(componentId).asInstanceOf[T]
  }
  def setComponent(e:Entity,c:T):Unit = {
    ensureComponentID(e)
    e.setComponentUnSafe(componentId,c)
  }
}

final class Entity extends Poolable with ECSNode{
  var parent :ECSNode = null
  private var _scene :Scene = null
  private [ecs] def setScene(scene:Scene) = _scene = scene
  def scene: Scene = if(_scene == null) throw new IllegalStateException("entity must be created by Scene so that it has proper scene set.") else _scene
  private val mComponents = new com.badlogic.gdx.utils.Array[Poolable]()
  private val mScriptMap = new ObjectMap[Class[_], Script]()
  private val mScripts = new DelayedArray[Script](script=>{
    mScriptMap.remove(script.getClass)
    val event = manual[RemoveScript]
    event.script = script
    fireModificationEvent(event)
    event.free
    script.freeToPool()
  })
  private val mModificationListeners = new DelayedRemovalArray[EntityEvent=>Unit]()
  val children = new DelayedArray[Entity](e=>{
    val event = manual[RemoveChild]
    event.child = e
    fireModificationEvent(event)
    event.free
    e.freeToPool()
  })

  def getComponentUnSafe(id:Int) = if(mComponents.size <= id) null else mComponents.get(id)
  def setComponentUnSafe(id:Int,c:Poolable) = {
    val required = id +1 - mComponents.size
    if(required > 0){
      mComponents.ensureCapacity(required)
      var i = 0
      while(i <= required){
        mComponents.add(null)
        i += 1
      }
    }
    mComponents.set(id,c)
  }
  def +=[T<:Component:IsComponent](c:T){
    implicitly[IsComponent[T]].setComponent(this,c)
  }
  def -=[T<:Component:IsComponent](){
    implicitly[IsComponent[T]].getComponent(this).freeToPool()
    implicitly[IsComponent[T]].setComponent(this,null.asInstanceOf[T])
  }
  def component[T<:Component:IsComponent]:T = implicitly[IsComponent[T]].getComponent(this)

  /**
   * the listener will be notified of the modification even before the listener is added.
   * @param listener
   */
  def addModificationListener(listener:EntityEvent=>Unit){
    mModificationListeners.add(listener)
    if(mScripts.size > 0){
      val it = mScripts.begin()
      while(it.hasNext){
        val e = manual[AddScript]
        e.script = it.next
        listener(e)
        e.free
      }
      mScripts.end()
    }
    if(children.size > 0){
      val it = children.begin()
      while(it.hasNext){
        val e = manual[AddChild]
        e.child = it.next()
        listener(e)
        e.free
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
  def +=(child: Entity):Entity = {
    children += child
    child.parent = this
    val event = manual[AddChild]
    event.child = child
    fireModificationEvent(event)
    event.free
    child
  }

  //TODO can this method be called while updating script? no!
  //TODO make this method accept invokation while updating script
  def -=(child: Entity) = children -= child

  def +=[T<:Script](script: T):T = {
    mScripts += script
    mScriptMap.put(script.getClass, script)
    val event = manual[AddScript]
    event.script = script
    fireModificationEvent(event)
    event.free
    script
  }

  def -=(script: Script) = mScripts -= script

  def script[T<:Script:Class]:T = mScriptMap.get(implicitly[Class[T]]).asInstanceOf[T]

  def updateScripts(delta:Float){
    //log("updateScript")
    val it = mScripts.begin()
    while(it.hasNext){
      val script = it.next()
      if(!script.isInitialized)script.initialize(this)
      script.update(delta)
    }
    mScripts.end()
    val it2 = children.begin()
    while(it2.hasNext){
      val next = it2.next()
      assert(next != this)
      next.updateScripts(delta)
    }
    children.end()
  }

  def reset() {
    {//clear scripts
      val it = mScripts.begin()
      while (it.hasNext) {
        it.next.freeToPool()
      }
      mScripts.end()
      mScripts.clear()
      mScriptMap.clear()
    }
    
    {//clear children
      val it = children.begin()
      while(it.hasNext){
        it.next().freeToPool()
      }
      children.end()
      children.clear()
    }
    {// clear components
     val it = mComponents.iterator()
      while(it.hasNext){
        val c = it.next()
        if(c != null)c.freeToPool()
      }
      mComponents.clear()
    }

    mModificationListeners.clear()
    _scene = null
    parent = null
  }

  def remove() {
    parent -= this
  }
}
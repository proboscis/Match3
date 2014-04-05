package com.glyph._scala.lib.libgdx.particle

import com.glyph._scala.lib.util.pool.{Pooling, Poolable}
import com.glyph._scala.lib.util.Logging
import com.glyph._scala.lib.util.updatable.Updatable
import com.badlogic.gdx.math.{Vector2, Matrix3}
import com.glyph._scala.lib.libgdx.PooledStack
import com.badlogic.gdx.graphics.Color
import scala.collection.mutable.ArrayBuffer
import scala.annotation.tailrec

/**
 * @author glyph
 */
object PEntity{
  val tmp = new Vector2
  implicit object PoolingMatrix3 extends Pooling[Matrix3]{
    override def newInstance: Matrix3 = new Matrix3
    override def reset(tgt: Matrix3): Unit = tgt.idt()
  }
  val matrixStack = new PooledStack[Matrix3]
  matrixStack.push()
  implicit object PoolingPSystem extends Pooling[PEntity]{
    override def newInstance: PEntity = new PEntity
    override def reset(tgt: PEntity): Unit = tgt.reset()
  }
}

//should i introduce entity component system? but it seems it's over complicated.
final class PEntity extends Updatable with Logging with Poolable{
  import PEntity._
  val transform = new Matrix3
  val vel = new Vector2
  val acc = new Vector2
  var parent:PEntity = null
  var weight = 0f
  var viscosity = 0f
  var elapsedTime = 0f
  val color = new Color()
  //this means that the entity is actually a thing in the world.
  //may be scaling is required too...?
  //for now, i stick to the traditional way of implementing this for particles only.
  private val addChildrenQueue = ArrayBuffer[PEntity]()
  private val removeChildrenQueue = ArrayBuffer[PEntity]()
  private val addModifierQueue = ArrayBuffer[PModifier]()
  private val removeModifierQueue = ArrayBuffer[PModifier]()
  private val mChildren = ArrayBuffer[PEntity]()
  private val mModifiers = ArrayBuffer[PModifier]()
  def children:IndexedSeq[PEntity] = mChildren
  def modifiers:IndexedSeq[PModifier] = mModifiers
  def +=(c:PModifier) = addModifierQueue += c
  def +=(c:PEntity) = addChildrenQueue += c
  def -=(c:PModifier) = removeModifierQueue += c
  def -=(c:PEntity) = removeChildrenQueue += c
  private def processChildrenModification(){
    if(!addChildrenQueue.isEmpty){
      var i = 0
      val l = addChildrenQueue.size
      while(i < l){
        val child = addChildrenQueue(i)
        child.parent = this
        mChildren += child
        i += 1
      }
      addChildrenQueue.clear()
    }
    if(!removeChildrenQueue.isEmpty){
      var i = 0
      val l = removeChildrenQueue.size
      while(i < l){
        val child = removeChildrenQueue(i)
        mChildren -= child
        child.parent = null
        child.freeToPool()//beware that any removed child is freed to the pool
        i += 1
      }
      removeChildrenQueue.clear()
    }
  }
  private def processModifierModification(){
    if(!addModifierQueue.isEmpty){
      mModifiers ++= addModifierQueue
      addModifierQueue.clear()
    }
    if(!removeModifierQueue.isEmpty){
      var i = 0
      val l = removeModifierQueue.size
      while(i < l){
        val c = removeModifierQueue(i)
        mModifiers -= c
        c.freeToPool()// should i free?? yes, for now...
        i += 1
      }
      removeModifierQueue.clear()
    }
  }
  override def update(delta: Float): Unit = {
    super.update(delta)
    elapsedTime += delta
    //update components
    var i = 0
    val componentsSize = mModifiers.length
    while ( i < componentsSize){
      mModifiers(i).onUpdate(this,delta)
      i += 1
    }
    processChildrenModification()
    processModifierModification()
    //TODO consider weight and viscosity
    vel.add(tmp.set(acc).scl(delta))
    transform.translate(tmp.set(vel).scl(delta))
    acc.set(0,0)
    val size = mChildren.size
    @tailrec
    def loop(ii:Int):Unit = if(ii < size){
      mChildren(ii).update(delta)
      loop(ii+1)
    }
    loop(0)
  }
  def calculateTransformHierarchy(){
    calculateWorldTransform(matrixStack)
  }
  def calculateWorldTransform(stack:PooledStack[Matrix3]){
    val prev = stack.head
    val size = mChildren.size
    stack.push().set(prev).mul(transform)
    onWorldTransform(stack.head)
    @tailrec
    def loop(i:Int):Unit = if(i < size){
      mChildren(i).calculateWorldTransform(stack)
      loop(i+1)
    }
    loop(0)
    stack.pop()
  }

  /**
   * call super method or components won't be notified of this method call
   * @param world
   */
  def onWorldTransform(world:Matrix3){
    //err(world)

    var i = 0
    val l = mModifiers.length
    while(i < l){
      mModifiers(i).onWorldTransform(world)
      i += 1
    }
  }
  def reset(){
    parent = null
    //notify components of disposal
    val componentSize = mModifiers.size
    var i= 0
    while(i < componentSize){
      val c = mModifiers(i)
      c.onDispose(this)
      c.freeToPool()
      i += 1
    }
    mModifiers.clear()
    //dispose all children
    val size = mChildren.size
    i = 0
    while ( i < size){
      mChildren(i).freeToPool()
      i+= 1
    }
    mChildren.clear()
    //reset values
    transform.idt()
    vel.set(Vector2.Zero)
    acc.set(Vector2.Zero)
    weight = 0f
    elapsedTime = 0f
    viscosity = 0f
    color.set(1,1,1,1)
  }
}
package com.glyph.scala.lib.util

import com.badlogic.gdx.utils.Pool

/**
 * @author glyph
 */
class LinkedList [T]extends Traversable[T]{
  val mHead = new Element
  private val iteratorPool = new IteratorPool
  private val elementPool = new ElementPool


  def push(e:T){
    val next = elementPool.obtain()
    next.data = e
    next.next = mHead.next
    mHead.next = next
  }
  def pop():T={

    val result = mHead.next
    mHead.next = result.next

    val data = result.data
    elementPool.free(result)
    data
  }


  def remove(t:T){
    var current = mHead.next
    var prev = mHead
    var continue = current.next != null
    while(continue){
      if (current.data == t){
        prev.next = current.next
        elementPool.free(current)
        continue = false
      }else{
        prev = current
        current = current.next
      }
    }
  }

  def foreach[U](f: (T) => U) {
    var current = mHead
    while(current != null && current.next != null){
      current = current.next
      f(current.data)
    }
  }

  def foreachPop[U](f:(T)=>U){
    while (!isEmpty){
      f(pop())
    }
  }
  def clear(){
    while(!isEmpty){
      pop()
    }
  }

  class Element{
    var data:T = null.asInstanceOf[T]
    var next:Element = null
  }
  def iterator=iteratorPool.obtain()

  class Iterator{
    var current:Element = null
    def init(){current = mHead.next}
    init()
    def hasNext():Boolean = current.next != null
    def next():T={
      val data = current.data
      current = current.next
      data
    }
  }


  override def isEmpty: Boolean = {
    mHead.next == null
  }

  class IteratorPool extends Pool[Iterator]{
    def newObject() = new Iterator
  }
  class ElementPool extends Pool[Element]{
    def newObject() = new Element

    override def free(obj: Element) {
      obj.data = null.asInstanceOf[T]
      obj.next = null
      super.free(obj)
    }
  }
}

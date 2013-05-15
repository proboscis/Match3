package com.glyph.scala.lib.util

import com.badlogic.gdx.utils.Pool
import collection.mutable

/**
 * @author glyph
 */
class LinkedList [T]extends mutable.Traversable[T]{
  val mHead = new Element
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

  override def foreach[U](f: (T) => U) {
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

  override def isEmpty: Boolean = {
    mHead.next == null
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

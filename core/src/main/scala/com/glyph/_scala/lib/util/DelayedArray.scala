package com.glyph._scala.lib.util

import com.badlogic.gdx.utils.{Array => GdxArray}
import com.glyph._scala.lib.util.pool.{Poolable, Pool}
import com.glyph._scala.game.Glyphs
import Glyphs._

/**
 * @author glyph
 *         badlogic array that keeps removal until it finishes iteration, and perform removal with specified function.
 *         nested iteration is supported.
 *         any modification done to this array will be suspended until its nest will be cleared by calling end()
 */
class DelayedArray[T](onRemove: T => Unit) {
  val elements = new GdxArray[T]
  private val nest = new GdxArray[DelayedArrayIterator[T]]
  private val addQueue = new GdxArray[T]
  private val removeQueue = new GdxArray[T]

  def isIterating = nest.size > 0

  def begin(): java.util.Iterator[T] = {
    val itr = DelayedArrayIterator(elements.items.asInstanceOf[Array[T]], elements.size)
    nest.add(itr)
    itr
  }

  def end() {
    nest.pop().freeToPool()
    if (!isIterating) {
      //remove queued objects
      while (removeQueue.size > 0) {
        val removing = removeQueue.pop()
        elements.removeValue(removing, true)
        onRemove(removing)
      }
      //add queued objects
      while (addQueue.size > 0) {
        elements.add(addQueue.pop())
      }
    }
  }

  def +=(e: T) {
    if (isIterating) {
      addQueue.add(e)
    } else {
      elements.add(e)
    }
  }

  def -=(e: T) {
    if (isIterating) {
      removeQueue.add(e)
    } else {
      elements.removeValue(e, true)
      onRemove(e)
    }
  }

  def size = elements.size

  def clear() {
    if (isIterating) throw new RuntimeException("you cannot clear this array while iterating")
    elements.clear()
  }
}

object DelayedArrayIterator {
  val pool = Pool(() => new DelayedArrayIterator[Object])(_.reset())(1000)

  def apply[T](elements: Array[T], size: Int): DelayedArrayIterator[T] = {
    val itr = pool.auto.asInstanceOf[DelayedArrayIterator[T]]
    itr.init(elements, size)
    itr
  }
}

class DelayedArrayIterator[T] extends java.util.Iterator[T] with Poolable {
  var array: Array[T] = null
  var index = 0
  var size = 0

  def hasNext: Boolean = index < size

  def next(): T = {
    index += 1
    array(index - 1)
  }

  def remove() = throw new RuntimeException("unsupported exception")

  def reset() {
    array = null
    index = 0
    size = 0
  }

  def init(elements: Array[T], size: Int) {
    array = elements
    this.size = size
    index = 0
  }
}

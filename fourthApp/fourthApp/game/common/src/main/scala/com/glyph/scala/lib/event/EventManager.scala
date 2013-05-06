package com.glyph.scala.lib.event

import collection.mutable.ListBuffer
import com.glyph.scala.Glyph
import annotation.tailrec
import com.glyph.scala.lib.util.LinkedList

/**
 * eventmanager with function registers
 */
class EventManager(final val TAG:String = "EventManager") {
  private val DEBUG = false
  val listenerMap = collection.mutable.HashMap.empty[Manifest[_], ListBuffer[Any]]
  var callDepth = 0
  private val children = new LinkedList[EventManager]

  /**
   * add callback
   * @param func
   * @param typ
   * @tparam T
   * @return
   */
  def +=[T](func: (T) => Boolean)(implicit typ: Manifest[T]) {
    val list = listenerMap get typ match {
      case Some(x) => x
      case None => {
        if(DEBUG)Glyph.log(TAG, "create new list for:" + typeStr[T])
        val newList = ListBuffer.empty[Any]
        listenerMap(typ) = newList
        newList
      }
    }
    if(DEBUG) Glyph.log(TAG, "attached callback to:" + typeStr[T])
    list += func
  }

  /**
   * remove callback
   * @param func
   * @param typ
   * @tparam T
   * @return
   */
  def -=[T](func: (T) => Boolean)(implicit typ: Manifest[T]) {
    listenerMap get typ match {
      case Some(list) => {
        list -= func
        if(DEBUG) Glyph.log(TAG, "successfully removed callback from:" + typeStr[T])
      }
      case _ => if(DEBUG) Glyph.log(TAG, "failed to remove callback from:" + typeStr[T])
    }
  }

  /**
   * dispatch event
   * @param event
   * @param typ
   * @tparam T
   * @return
   */
  def dispatch[T](event: T)(implicit typ: Manifest[T]) {
    if (DEBUG) {
      debugDispatch(event)
    } else {
      if (typ.runtimeClass.isAnnotationPresent(classOf[DebugEvent])) {
        debugDispatch(event)
      } else {
        onlyDispatch(event)
      }
    }
    children.foreach{
      _.dispatch[T](event)
    }
  }

  private def onlyDispatch[T](event: T)(implicit typ: Manifest[T]) {
    listenerMap get typ match {
      case Some(listeners) => {
        @tailrec
        def loop(it: Iterator[Any]) {
          if (it.hasNext) {
            val next = it.next()
            if (next.asInstanceOf[(T) => Boolean](event)) {
              return
            } else {
              loop(it)
            }
          } else {
          }
        }
        loop(listeners.iterator)
      }
      case None =>
    }
    callDepth -= 1
  }

  private def debugDispatch[T](event: T)(implicit typ: Manifest[T]) {
    callDepth += 1
    var depth = ""
    (1 to callDepth) foreach {
      _ =>
        depth += "="
    }
    depth += ">"
    // depth = ("=" * callDepth) + ">"
    Glyph.log(TAG, depth + typeStr[T])
    listenerMap get typ match {
      case Some(listeners) => {
        @tailrec
        def loop(it: Iterator[Any]) {
          if (it.hasNext) {
            val next = it.next()
            val callback = next.asInstanceOf[(T) => Boolean]
            val proceed = callback(event)
            val funcType = Manifest.classType(callback.getClass)
            if (proceed) {
              Glyph.log(TAG, depth + "event handled by:" + funcType.runtimeClass)
              return
            } else {
              Glyph.log(TAG, depth + "event not handled by:" + funcType.runtimeClass)
              loop(it)
            }
          } else {
            Glyph.log(TAG, depth + "event is passed to all listeners:" + typeStr[T])
          }
        }
        loop(listeners.iterator)
      }
      case None => Glyph.log(TAG, depth + "no listener:" + typeStr[T])
    }
    callDepth -= 1
  }

  def typeStr[T](implicit typ: Manifest[T]): String = {
    typ.runtimeClass.getSimpleName
  }
  def addChild(manager:EventManager){
    children.push(manager)
  }
  def removeChild(manager:EventManager){
    children.remove(manager)
  }
}

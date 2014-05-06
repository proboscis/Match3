package com.glyph._scala.lib.event

import scala.collection.mutable
import com.badlogic.gdx.utils.{Array => GdxArray,SnapshotArray=>SArray, ObjectMap}
/**
 * @author glyph
 */
class EventManager {
  val listenerMap = new ObjectMap[Class[_],SArray[Any => Unit]]()
  /**
   * register the callback and returns the given callback for future use.
   * @param listener
   * @tparam T
   * @return
   */
  def +=[T:Class](listener:T=>Unit):T=>Unit ={
    val key = implicitly[Class[T]]
    var listeners = listenerMap.get(key)
    if(listeners == null){
      listeners = new SArray[Any=>Unit]
      listenerMap.put(key,listeners)
    }
    listeners.add(listener.asInstanceOf[Any=>Unit])
    listener
  }
  def -=[T:Class](listener:T=>Unit){
    val key = implicitly[Class[T]]
    val listeners = listenerMap.get(key)
    if(listeners != null){
      listeners.removeValue(listener.asInstanceOf[Any=>Unit],true)
    }
  }
  def <<[T:Class](event:T){
    val key = implicitly[Class[T]]
    val listeners = listenerMap.get(key)
    if(listeners != null){
      val elems = listeners.begin().asInstanceOf[Array[Object]]
      var i = 0
      val size = listeners.size
      while(i<size){
        elems(i).asInstanceOf[Any=>Unit](event)
        i+= 1
      }
      listeners.end()
    }
  }
}
import com.glyph.ClassMacro._
object EventManagerTest{
  def main(args: Array[String]) {
    val em = new EventManager
    em += ((e:Int)=>{})
    val map = new mutable.HashMap[AnyRef,AnyRef]
    while(true){
      var i = 0
      while(i < 100000){
        em << i
        i+=1
      }
      Thread.sleep(100)
    }
  }
}
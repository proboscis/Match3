package com.glyph.scala.event

import collection.mutable.ListBuffer
import com.glyph.scala.Glyph

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/07
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
class EventManager {
  private val TAG = "EventManager"
  val listenerMap = collection.mutable.HashMap.empty[Manifest[_],ListBuffer[Any]]

  /**
   * add callback
   * @param func
   * @param typ
   * @tparam T
   * @return
   */
  def +=[T](func: (T)=>Boolean )(implicit typ: Manifest[T]){
    val list = listenerMap get typ match{
      case Some(x) => x
      case None => {
        Glyph.log(TAG,"create new list for:"+typ)
        val newList = ListBuffer.empty[Any]
        listenerMap(typ) = newList
        newList
      }
    }
    Glyph.log(TAG,"attached callback to:"+typ)
    list += func
  }

  /**
   * remove callback
   * @param func
   * @param typ
   * @tparam T
   * @return
   */
  def -=[T](func: (T) => Boolean)(implicit typ: Manifest[T]){
    listenerMap get typ match{
      case Some(list) =>{
        list -= func
        Glyph.log(TAG,"successfully removed callback from:"+typ)
      }
      case _ =>Glyph.log(TAG,"failed to remove callback from:"+typ)
    }
  }

  /**
   * dispatch event
   * @param event
   * @param typ
   * @tparam T
   * @return
   */
  def <= [T](event: T)(implicit typ:Manifest[T]){
    listenerMap get typ match {
      case Some(listeners) => {
        listeners.foreach(_.asInstanceOf[(T)=>Boolean](event))
        Glyph.log(TAG,"dispatch event:"+typ)
      }
      case None => Glyph.log(TAG,"no listener:"+typ)
    }
  }

  implicit def type2String[T](typ :Manifest[T]){
    typ.runtimeClass.getSimpleName
  }
}

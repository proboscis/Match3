package com.glyph._scala.lib.util

import scala.collection.mutable.ArrayBuffer
import scalaz.Memo
import com.glyph._scala.lib.util.pool.{Pool, Poolable}
import com.glyph.ClassMacro._

trait Poly[R]{
  def apply[P](param:P):R
}
//TODO implement later... since this is not required immediately
class TypeClassBuffer[TypeClass[_],Interface<:AnyRef](converterSrc:(TypeClass[AnyRef],AnyRef)=>Interface) extends Traversable[Interface]{
  import TypeClassBuffer._
  import Pool._
  val elements = new ArrayBuffer[MTuple[AnyRef,Interface]]()
  val converter = Memo.mutableHashMapMemo.apply(converterSrc.tupled)
  def -=(e:AnyRef)={
    var found:MTuple[AnyRef,Interface] = null
    var i = 0
    val l = elements.length
    while(i < l && found == null){
      val value = elements(i)
      if(value.a == e){
        found = value
      }
    }
    if(found != null){
      elements -= found
      found.asInstanceOf[MTuple[AnyRef,AnyRef]].free
    }
  }
  def +=[T<:AnyRef:TypeClass](e:T) = elements += {
    val i = converter(implicitly[TypeClass[T]].asInstanceOf[TypeClass[AnyRef]],e)
    val tuple = TypeClassBuffer.pool.manual
    tuple.initialize(e,i)
    tuple.asInstanceOf[MTuple[AnyRef,Interface]]
  }
  override def foreach[U](f: (Interface) => U): Unit = {
    var i = 0
    val l = elements.size
    while(i < l){
      f(elements(i).b)
      i += 1
    }
  }
}
object TypeClassBuffer{
  private[TypeClassBuffer] implicit val pool :Pool[MTuple[AnyRef,AnyRef]] = Pool(()=>new MTuple[AnyRef,AnyRef](null,null))(_.reset())(1000)
}
case class MTuple[A,B](var a:A,var b:B){
  def initialize(newA:A,newB:B){
    a = newA
    b = newB
  }
  def reset(){
    a = null.asInstanceOf[A]
    b = null.asInstanceOf[B]
  }
}

object Mem{
  import scalaz._
  import Scalaz._
}
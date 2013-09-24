package com.glyph.scala.lib.util.reactive

/**
 * @author glyph
 */
class Divider[T:Numeric](val limit:Varying[T],initialCount:Int = 0) extends Varying[Int] with Reactor{
  private val evidence = implicitly[Numeric[T]]
  import evidence._
  val source = Var(evidence.zero)
  private val count = Var(initialCount)
  reactVar(source){
    s =>
      //println(s)
      if(gteq(s,limit())){
      count() += 1
      source()=zero
    }
  }
  reactVar(count){
    c => notifyObservers(c)
  }

  def current: Int= count()
  def <=(exp:T){
    source() = plus(source(),exp)
  }
  def -=(p:Int){
    count() -= p
  }
}

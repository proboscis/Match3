package com.glyph._scala.lib.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.{Application => App}
import scalaz._
import Scalaz._
/**
 * @author glyph
 */
trait Logging {
  type Log2 = App=>String=>String=>Unit
  type Log3 = App=>String=>String=>Throwable=>Unit
  private def app = Option(Gdx.app)
  private def printer = (tag:String)=>(msg:String)=>println(tag+":"+msg)
  private def printerE = (tag:String)=>(msg:String)=>(e:Throwable) => println(tag+":"+msg+"\n"+e.getStackTraceString)
  private def applyName[R] = (f:(String)=>R) => f(this.getClass.getName.replaceAll("anonfun|anon","@").replaceAll("\\$","-")+"_%8x".format(System.identityHashCode(this)))
  private def curry2[P,Q,R]:((P,Q)=>R)=>P=>Q=>R=f=>p=>q=>f(p,q)
  private def curry3[O,P,Q,R]:((O,P,Q)=>R)=>O=>P=>Q=>R=f=>o=>p=>q=>f(o,p,q)
  private def appLog:Log2 = ap => curry2(ap.log)
  private def appDebug:Log2 = ap => curry2(ap.debug)
  private def appError:Log2 = ap => curry2(ap.error)
  private def appDebugE:Log3 = ap => curry3(ap.debug)
  private def appErrorE:Log3 = ap => curry3(ap.error)
  private def toAny[R] = (f:(String)=>R) => (any:Any) => f(any.toString)
  private def toAnyFunc[R] =(logger:App=>String=>String=>R)=>(default:String=>String=>R)=> (app map logger) | default |> applyName |> toAny[R]
  private def toAnyDefault[R] = toAnyFunc(_:Log2)(printer)
  private def toAnyDefaultE[R] = toAnyFunc(_:Log3)(printerE)
  protected def log = appLog |> toAnyDefault
  protected def deb = appDebug |> toAnyDefault
  protected def err = appError |> toAnyDefault
  protected def debE = appDebugE |> toAnyDefaultE
  protected def errE = appErrorE |> toAnyDefaultE
}
trait Threading extends Logging{
  override def log = (any:Any) =>  super.log(any,"tid:"+Thread.currentThread().getId)
}
trait Timing{
  self:Logging=>
  def printTime(tag:String)(block: =>Unit){
    val prev = System.nanoTime()
    block
    val finish = System.nanoTime()
    log("(%s,%s)".format(getClass.getSimpleName,tag)+(finish-prev))
  }
}
trait HeapMeasure{
  self:Logging=>
  def printHeap(tag:String)(block: => Unit){
    val runtime = Runtime.getRuntime
    val prev = runtime.totalMemory() - runtime.freeMemory()
    block
    val current =  runtime.totalMemory() - runtime.freeMemory()
    log("heap increase:"+(current-prev)/1000+"kb")
  }
}
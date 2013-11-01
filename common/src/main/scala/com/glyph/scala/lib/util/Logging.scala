package com.glyph.scala.lib.util

import com.badlogic.gdx.Gdx
import scalaz._
import Scalaz._
/**
 * @author glyph
 */
trait Logging {
  private def app = Option(Gdx.app)
  private def printer = (a:String,b:String)=>println(a+":"+b)
  private def applyName = (f:(String,String)=>Unit) => f(this.getClass.getSimpleName,_:String)
  private def appLog = app.map{ap=>ap.log(_:String,_:String)}
  private def appDebug = app.map{ap=>ap.debug(_:String,_:String)}
  private def appError = app.map{ap=>ap.error(_:String,_:String)}
  private def toAny = (f:(String)=>Unit) => (any:Any) => f(any.toString)
  private def optF2ToAnyF = (optf:Option[(String,String)=>Unit]) => optf | printer |> applyName |> toAny
  def log = appLog |> optF2ToAnyF
  def deb = appDebug |> optF2ToAnyF
  def err = appError |> optF2ToAnyF
}

package com.glyph.scala

import com.badlogic.gdx.Gdx

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/07
 * Time: 0:40
 * To change this template use File | Settings | File Templates.
 */
object Glyph {
  val TAG = "com.glyph:"
  def log( str: String) ={
    Gdx.app.log(TAG,str)
  }
  def log(tag: String , str: String) ={
    Gdx.app.log(TAG+tag,str)
  }
  def printExecTime(tag:String,func: =>Unit){
    val prev = System.nanoTime();
    Glyph.log(tag,"=> start")
    func
    Glyph.log(tag,"<= "+(System.nanoTime()-prev)/1000/1000)+"ms";
  }
}

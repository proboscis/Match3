package com.glyph._scala.lib.util.gl

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.Gdx
import scala.collection.mutable

/**
 * @author glyph
 */
object ViewportStack {
  private val stack = new mutable.ArrayStack[Rectangle]()
  private var current :Rectangle = Dummy
  stack.push(current)
  def push(v:Rectangle){
    stack.push(current)
    current = v
    Gdx.gl.glViewport(v.getX.toInt,v.getY.toInt,v.getWidth.toInt,v.getHeight.toInt)
  }
  def pop(){
    val v = stack.pop()
    if (v == Dummy){
      v.set(0,0,Gdx.graphics.getWidth,Gdx.graphics.getHeight)
    }
    current = v
    Gdx.gl.glViewport(v.getX.toInt,v.getY.toInt,v.getWidth.toInt,v.getHeight.toInt)
  }
  private object Dummy extends Rectangle
}

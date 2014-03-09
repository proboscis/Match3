package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch}
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.Gdx
import scala.collection.mutable
import com.badlogic.gdx.graphics.Camera

/**
 * @author glyph
 */

trait Scissor extends Actor {
  private val clipBounds = new Rectangle()
  private val scissors = new Rectangle()
  //you have to make this be able to use the ... different camera.
  override def draw(batch:Batch, parentAlpha: Float) {
    batch.flush()
    clipBounds.set(getX, getY, getWidth, getHeight)
    val info = Scissor.head
    val camera = if(info != null) info._1 else getStage.getCamera
    var x,y,w,h:Float = 0f
    if (info == null){
      w = Gdx.graphics.getWidth
      h = Gdx.graphics.getHeight
    }else{
      val vp = info._2
      x = vp.x
      y = vp.y
      w = vp.width
      h = vp.height
    }
    ScissorStack.calculateScissors(camera,x,y,w,h,batch.getTransformMatrix, clipBounds, scissors)
    if (ScissorStack.pushScissors(scissors)) {
      super.draw(batch, parentAlpha)
      onScissor(batch,parentAlpha)
      ScissorStack.popScissors()
    } else {
      super.draw(batch, parentAlpha)
    }
    batch.flush()
  }
  def onScissor(batch:Batch,parentAlpha:Float){}
}
object Scissor{
  type Viewport = Rectangle
  type ScissorInfo = (Camera,Viewport)
  val infoStack = new mutable.Stack[ScissorInfo]()
  def push(info:ScissorInfo){
    infoStack.push(info)
  }
  def pop(){
    assert(!infoStack.isEmpty)
    infoStack.pop()
  }
  def head = if (infoStack.isEmpty)null else infoStack.head
}
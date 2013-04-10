package com.glyph.scala.lib.entity_component_system.math

import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.Gdx

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/02
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
class Vec2( a:Float,b:Float) extends Vector2(a,b){
  def this () = this(0,0)
  def -= = (t:Vector2) =>{
    x = x - t.x
    y = y - t.y
    this
  }
  def += = (t:Vector2) => {
    x = x + t.x
    y = y + t.y
    this
  }
  def *= (t:Vector2){
    x = x * t.x
    y = y * t.y
    this
  }
  def /= (t:Vector2){
    x = x / t.x
    y = y / t.y
    this
  }

  def + (t:Vector2)= new Vec2(x+t.x, y+t.y)
  def - (t:Vector2)= new Vec2(x-t.x,y-t.y)
  def * (t:Vector2)= new Vec2(x*t.x,y*t.y)
  def / (t:Vector2)= new Vec2(x/t.x, y/t.y)
}
object Vec2{
  /**
   * creates a new normalized vector with random direction
   * @return
   */
  def random = new Vec2(1,0).rotate(MathUtils.random(360))
}

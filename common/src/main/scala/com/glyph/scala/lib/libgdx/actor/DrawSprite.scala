package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch, Sprite}
import com.glyph.scala.lib.math.Vec2
import com.badlogic.gdx.graphics.Color

trait DrawSprite extends Actor {
  def drawSprite(batch:Batch,sprite:Sprite,parentAlpha:Float,offset:Vec2 = null,color:Color = getColor){
    sprite.setSize(getWidth, getHeight)
    sprite.setOrigin(getOriginX,getOriginY)
    if (offset != null){
      sprite.setPosition(getX+offset.x, getY+offset.y)
    }else{
      sprite.setPosition(getX,getY)
    }
    sprite.setScale(getScaleX, getScaleY)
    sprite.setRotation(getRotation)
    sprite.setColor(color)
    sprite.draw(batch, parentAlpha)
  }
}
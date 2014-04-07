package com.glyph._scala.lib.ecs.system

import com.glyph._scala.lib.ecs.Scene
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

/**
 * @author glyph
 */
class SpriteRenderer(combined:Matrix4) extends EntitySystem{
  val batch = new SpriteBatch(1000)
  val sprites = new ArrayBuffer[Sprite]
  val render = (s:Sprite)=>{
    s.draw(batch)
  }
  override def update(scene: Scene, delta: Float): Unit = {}

  override def draw(scene: Scene): Unit ={
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    batch.setProjectionMatrix(combined)
    batch.begin()
    sprites foreach render
    batch.end()
  }
}

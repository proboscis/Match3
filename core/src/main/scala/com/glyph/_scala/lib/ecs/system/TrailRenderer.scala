package com.glyph._scala.lib.ecs.system
import com.glyph._scala.lib.ecs.Scene
import com.badlogic.gdx.math.Matrix4
import com.glyph._scala.lib.libgdx.gl.{Trail, UVTrail, TypedBatch}
import TypedBatch._
import com.badlogic.gdx.utils.DelayedRemovalArray
import com.badlogic.gdx.graphics.{GL20, Camera, Texture}
import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */
class TrailRenderer(combined:Matrix4,texture:Texture) extends EntitySystem{
  val batch = new TypedBatch[UVTrail](1000*10*2)
  private val trails = new DelayedRemovalArray[UVTrail]()
  def +=(t:UVTrail) = trails.add(t)
  def -=(t:UVTrail) = trails.removeValue(t,true)
  override def update(scene: Scene, delta: Float): Unit = {}
  override def draw(scene: Scene): Unit = {
    Gdx.gl.glEnable(GL20.GL_TEXTURE_2D)
    Gdx.gl.glEnable(GL20.GL_BLEND)
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    texture.bind()
    batch.combined.set(combined)
    batch.begin()
    trails.begin()
    val it = trails.iterator()
    while(it.hasNext){
      batch.draw(it.next())
    }
    trails.end()
    batch.end()
  }
}

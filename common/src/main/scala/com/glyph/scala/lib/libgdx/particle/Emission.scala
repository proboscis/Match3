package com.glyph.scala.lib.libgdx.particle

import com.glyph.scala.lib.util.reactive.{EventSource, Varying}
import com.badlogic.gdx.math.{MathUtils, Vector2, Rectangle}
import com.glyph.java.particle.{SpriteParticle, ParticlePool}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.GL10

class Emission(area: Varying[Rectangle], p: ParticlePool[SpriteParticle], duration: Float,alpha:Float,power:Float) extends ParticleContainer {
  val center = area map {
    r => new Vector2(r.x + r.width / 2, r.y + r.height / 2)
  }
  var timer = duration
  val finishEvent = EventSource[Unit]()
  val hitEvent =EventSource[SpriteParticle]()
  def pool: ParticlePool[SpriteParticle] = p

  override def act(delta: Float) {
    timer -= delta
    if (timer < 0) {
      finish()
    } else {
      particles foreach {
        p =>
          if (area().contains(p.getX + p.getWidth / 2, p.getY + p.getHeight / 2)) {
            particles = particles diff p :: Nil
            hitEvent.emit(p)
            pool.free(p)
            if(particles.isEmpty){
              finish()
            }
          } else {
            val diff = center().cpy.sub(p.getX, p.getY)
            p.getVelocity.add(diff.nor().scl(power*Math.pow(1-timer/duration,2).toFloat))
            //val len2 = p.getVelocity.len2()
            p.getVelocity.scl(0.9f)
          }
      }
      super.act(delta)
    }
  }
  private def finish(){
    particles foreach {hitEvent.emit}
    remove()
    dispose()
    finishEvent.emit(null)
  }

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    val src = batch.getBlendSrcFunc
    val dst = batch.getBlendDstFunc
    batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
    super.draw(batch, parentAlpha*alpha)
    //batch.flush()
    batch.setBlendFunction(src,dst)
  }
}
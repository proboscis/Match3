package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.graphics.{GL10, Color, Texture}
import com.badlogic.gdx.Gdx
import aurelienribon.tweenengine._
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Sprite}
import com.glyph.scala.lib.libgdx.actor.SpriteBatchRenderer
import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.glyph.scala.lib.util.{Logging, Timing}

/**
 * @author glyph
 */
class ExplosionTest extends ConfiguredScreen with Logging with Timing {
  autoClearScreen = false
  val particleTexture = new Texture(Gdx.files.internal("data/particle.png"))
  implicit val tweenManager = new TweenManager()
  val renderer = new Group with SpriteBatchRenderer
  val timeLine = Timeline.createParallel()
  val shader = ShaderHandler("shader/default.vert", "shader/color.frag")
  val trails = 1 to 1000 map (_ => {
    import SpriteAccessor._
    import com.badlogic.gdx.math.MathUtils._
    val sp = manual[Sprite]
    sp.setTexture(particleTexture)
    sp.asInstanceOf[TextureRegion].setRegion(0, 0, particleTexture.getWidth, particleTexture.getHeight)
    sp.setSize(10, 10)
    sp.setColor(Color.WHITE)
    sp.setPosition(random(0, STAGE_WIDTH), random(0, STAGE_HEIGHT))
    val trail = new Trail(10)

    timeLine.push(Tween.
      to(sp, XY, 1).
      target(random(0, STAGE_WIDTH), random(0, STAGE_HEIGHT)).
      ease(TweenEquations.easeOutExpo).setCallback(new TweenCallback {
      def onEvent(`type`: Int, source: BaseTween[_]): Unit = {
        trail.reset()
      }
    }).setCallbackTriggers(TweenCallback.COMPLETE))
    sp -> trail
  })
  val sprites = trails map {
    case (sp, _) => sp
  }
  timeLine.repeat(100, 0)
  timeLine.start(tweenManager)
  renderer.addDrawable(sprites)
  root.addActor(renderer)

  var time = 0f
  ShaderProgram.pedantic = false
  val trailRenderer = shader.applier {
    s =>
      s.begin()
      s.setUniformMatrix("u_projTrans", stage.getCamera.combined)
      //s.setUniformi("u_texture", 0)
      s.setUniformf("time", time)
      s.setUniformf("resolution", 1080, 1920)
      s.setUniformf("mouse", 0, 0)
      printTime("render trails") {
        trails foreach {
          case (sp, trail) => trail.mesh.render(s, GL10.GL_TRIANGLE_STRIP)
        }
      }
      s.end()
  }

  override def render(delta: Float): Unit = {
    clearScreen()
    tweenManager.update(delta)
    printTime("updateMesh") {
      trails foreach {
        case (sp, trail) => trail.add(sp.getX + sp.getWidth / 2, sp.getY + sp.getHeight / 2)
      }
    }
    trailRenderer()
    time += delta
    super.render(delta)
  }
}

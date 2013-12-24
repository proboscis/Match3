package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.gl.{BaseStripBatch, ShaderHandler}
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}

/**
 * @author glyph
 */
class TrailedParticleTest extends ScreenBuilder {
  def requiredAssets: Set[(Class[_], Seq[String])] = Set(classOf[Texture] -> Seq("data/particle.png"))
  def create(assetManager: AssetManager): Screen = new TrailTestEnvironment(
    ShaderHandler("shader/rotate2.vert", "shader/default.frag"),
    new BaseStripBatch(1000 * 10 * 2, UVTrail.ATTRIBUTES)) {
    implicit val am = assetManager
    val texture: Texture = "data/particle.png".fromAssets
    val pairs = TrailOps.createManualParticles(
      () => new UVTrail(10))(texture)(
        100)
    renderer addDrawable (pairs map (_._1))
    val box2DRenderer = new Box2DDebugRenderer()
    val world = new World(new Vector2(0, -10), true)
    import TrailedParticleTest._


    override def onShader(s: ShaderProgram): Unit = {
      super.onShader(s)
      texture.bind()
    }

    override def onBatch(s: ShaderProgram, batch: BaseStripBatch): Unit = {
      super.onBatch(s, batch)
      pairs foreach {
        case (sp, trail) => batch.draw(s, trail.meshVertices, trail.count)
      }
    }

    val scale = 0.1f
    val groundBodyDef = new BodyDef
    groundBodyDef.position.set(0,0)
    val rightBodyDef = new BodyDef
    rightBodyDef.position.set(STAGE_WIDTH*scale,0)
    val topBodyDef = new BodyDef
    topBodyDef.position.set(0,STAGE_HEIGHT*scale)
    val groundBox = new PolygonShape()
    groundBox.setAsBox(STAGE_WIDTH*scale, 0f)
    val wallBox = new PolygonShape()
    wallBox.setAsBox(0,STAGE_HEIGHT*scale)
    val bodySpTrails = pairs map {
      case (sp, trail) =>
        import MathUtils._
        val body = world.createBody(bodyDef)
        val fixture = body.createFixture(fixtureDef)
        body.setTransform(random(STAGE_WIDTH*scale), random(STAGE_HEIGHT*scale), 0)
        val p = body.getPosition
        body.applyLinearImpulse(random(1000), random(1000), p.x, p.y, true)
        (sp, trail, body)
    }
    val groundBody = world.createBody(groundBodyDef)
    groundBody.createFixture(groundBox, 0.0f)
    val rightBody = world.createBody(rightBodyDef)
    rightBody.createFixture(wallBox,0.0f)
    val leftBody = world.createBody(groundBodyDef)
    leftBody.createFixture(wallBox,0.0f)
    val topBody = world.createBody(topBodyDef)
    topBody.createFixture(groundBox,0.0f)
    groundBox.dispose()
    wallBox.dispose()

    val bodies = bodySpTrails.map(_._3)

    val gravity = new Vector2()
    override def render(delta: Float): Unit = {
      clearScreen()
      //box2DRenderer.render(world, stage.getCamera.combined)
      super.render(delta)
      if(Gdx.input.isTouched){
        val force = new Vector2
        val touch = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX, Gdx.input.getY))
        touch.scl(scale)
        bodies foreach {
          b =>
            val dif = force.set(b.getPosition).sub(touch)
            val dist = dif.len2()
            dif.scl(10/dist)
            b.applyForceToCenter(force, true)
        }
      }
      world.step(delta, 6, 2)
      bodySpTrails foreach {
        case (sp, trail, body) => {
          val p = body.getPosition
          p.scl(1/scale)
          sp.setPosition(p.x - sp.getWidth / 2, p.y - sp.getHeight / 2)
          trail.add(p.x, p.y)
        }
      }
      world.setGravity(gravity.set(-Gdx.input.getAccelerometerX*10,-Gdx.input.getAccelerometerY*10))
    }
  }
}

object TrailedParticleTest {
  val bodyDef = new BodyDef
  bodyDef.`type` = BodyType.DynamicBody
  bodyDef.position.set(0, 0)
  //TODO you have to dispose this shape after every thing is done.
  val circle = new CircleShape()
  //~defs dont need disposing
  circle.setRadius(1f)
  val fixtureDef = new FixtureDef()
  fixtureDef.shape = circle
  fixtureDef.density = 0.001f
  fixtureDef.friction = 0.0f
  fixtureDef.restitution = 0.6f
  fixtureDef.filter.groupIndex = -2
}

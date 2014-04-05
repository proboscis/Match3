package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.ecs.Scene
import com.glyph._scala.lib.ecs.script.{SimplePhysics, TrailHolder, Transform}
import com.glyph._scala.lib.ecs.system.TrailRenderer
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.ecs.script.task.EntityTaskProcessor
import com.glyph._scala.lib.util.updatable.task.{Block, Delay, Sequence}
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.glyph._scala.lib.ecs.Entity
import scalaz._
import Scalaz._
import com.glyph._scala.lib.ecs.script.particle.Absorber
import com.glyph._scala.lib.util.pool.GlobalPool._

/**
 * @author glyph
 */
class ECSTest extends ConfiguredScreen {
  val scene = new Scene
  val tmp = new Vector2
  val tmp2 = new Vector2
  val rootEntity = new Entity
  rootEntity += auto[Absorber] <| (_.area.set(STAGE_WIDTH / 2 - 100, STAGE_HEIGHT / 2 - 100, 200, 200))
  rootEntity += auto[SimplePhysics]
  rootEntity += auto[Transform]
  scene += rootEntity
  time("pre alloc") {
    preAlloc[Entity](2000)
    preAlloc[Transform](2000)
    preAlloc[TrailHolder](2000)
    preAlloc[SimplePhysics](2000)
    preAlloc[EntityTaskProcessor](2000)
  }
  def simpleTrail(velocity:Vector2,parent:Entity)={
    val e = auto[Entity]
    val trans = auto[Transform]
    val trail = auto[TrailHolder]
    val body = auto[SimplePhysics]
    e += trans
    e += trail
    e += body
    if (parent != null) {
      trans.matrix.setToTranslation(parent.getScript[Transform].matrix.getTranslation(new Vector2))
      val pBody = parent.getScript[SimplePhysics]
      body.vel.set(velocity).add(pBody.vel)
    }
    e
  }
  val particleGenerator: (Entity, Vector2) => Unit = (parent, vel) => {

    import MathUtils._
    def rand = random(-1f, 1f) * 400
    val processor = auto[EntityTaskProcessor]
    val generator = simpleTrail(new Vector2(rand,rand),parent)
    generator += processor
    rootEntity += generator
    val children = 1 to 50 map (_ => simpleTrail(new Vector2(rand,rand),parent))
    children foreach rootEntity.+=
    processor.add(Sequence(Delay(1f), Block {
      particleGenerator(generator,new Vector2(rand,rand))
      children foreach rootEntity.-=
    }))

  }
  scene += new TrailRenderer(stage.getCamera, "data/particle.png")
  rootEntity.getScript[Transform].matrix.setToTranslation(STAGE_WIDTH / 2, 0)
  particleGenerator(rootEntity,new Vector2())

  override def render(delta: Float): Unit = {
    super.render(delta)
    scene.update(delta)
    scene.draw()
  }
}

package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.ecs.{Scene, Entity}
import com.glyph._scala.lib.ecs.script.{Gravity, AreaSensor, TrailHolder}
import com.glyph._scala.lib.ecs.system.TrailRenderer
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.ecs.script.task.EntityTaskProcessor
import com.glyph._scala.lib.util.updatable.task.{Block, Delay, Sequence}
import com.badlogic.gdx.math.{Rectangle, MathUtils, Vector2}
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.pool.GlobalPool._
import com.glyph._scala.lib.ecs.component.{SimplePhysics, Velocities, Transform}

/**
 * @author glyph
 */
class ECSTest extends ConfiguredScreen {
  val scene = new Scene
  val tmp2 = new Vector2
  val rootEntity = scene.createEntity()
  //ALRIGHT, so i finish this or i never can start next production. and preparation for the international studying like going abroad

  // The fancy looking doesn't help players enjoy the game.
  // the graphics should have its reason to exist.
  // they must enhance the reasoning of the game system.
  // animations are required to show whats going on with the game.
  // and ornaments are there to provide the world view to the players. if the worlds is simple enough,
  // it won't be required.
  rootEntity += auto[Gravity] <| (g => {
    g.center.set(STAGE_WIDTH / 2f - 100f, STAGE_HEIGHT / 2f - 100f)
    g.power = 50f
  })
  rootEntity += new AreaSensor(new Rectangle(0,0,STAGE_WIDTH/3,STAGE_HEIGHT/3),_.remove)
  rootEntity += auto[Velocities]
  rootEntity += auto[Transform]
  rootEntity += auto[SimplePhysics]
  scene += rootEntity
  time("pre alloc") {
    preAlloc[Entity](2000)
    preAlloc[Transform](2000)
    preAlloc[TrailHolder](2000)
    preAlloc[Velocities](2000)
    preAlloc[EntityTaskProcessor](2000)
  }

  def simpleTrail(velocity: Vector2, parent: Entity) = {
    val e = scene.createEntity()
    val trans = auto[Transform]
    val trail = auto[TrailHolder]
    val body = auto[Velocities]
    e += auto[SimplePhysics]
    e += trans
    e += trail
    e += body
    if (parent != null) {
      trans.matrix.set(parent.component[Transform].matrix)
      val pBody = parent.component[Velocities]
      body.vel.set(velocity).add(pBody.vel)
    }
    e
  }

  val particleGenerator: (Entity) => Unit = (parent) => {
    import MathUtils._
    def rand = random(-1f, 1f)
    val processor = auto[EntityTaskProcessor]
    val generator = simpleTrail(tmp2.set(rand, rand).nor().scl(300), parent)
    generator += processor
    rootEntity += generator
    val children = 1 to 50 map (_ => simpleTrail(tmp2.set(rand, rand).nor().scl(300), parent))
    children foreach rootEntity.+=
    processor.addTask(Sequence(Delay(1f), Block {
      particleGenerator(generator)
      //children foreach rootEntity.-=
    }))

  }
  scene += new TrailRenderer(stage.getCamera.combined, "data/particle.png")
  rootEntity.component[Transform].matrix.setToTranslation(STAGE_WIDTH / 2, STAGE_HEIGHT / 2)
  particleGenerator(rootEntity)

  override def render(delta: Float): Unit = {
    super.render(delta)
    scene.update(delta)
    scene.draw()
  }
}

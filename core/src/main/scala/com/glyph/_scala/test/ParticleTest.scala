package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.math._
import com.glyph._scala.lib.libgdx.gl._
import com.badlogic.gdx.graphics.{Color, GL20, Texture, VertexAttributes}
import scala.{annotation, collection, Some}
import scala.collection.mutable.ArrayBuffer
import com.glyph._scala.lib.util.updatable.Updatable
import scala.annotation.{target, tailrec}
import com.glyph._scala.lib.util.pool.{PoolOps, Pool, Pooling, Poolable}
import com.glyph._scala.lib.util.updatable.task.Accessor
import com.glyph._scala.lib.libgdx.{PooledStack, AssetDescriptor}
import com.glyph.ClassMacro
import ClassMacro._
import com.glyph._scala.lib.util.Logging
import scala.collection.mutable
import com.glyph._scala.lib.libgdx.particle._
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.pool.GlobalPool._
import PoolOps._
import com.glyph._scala.lib.util.pooling_task.PoolingOps._
import MathUtils._
/**
 * @author proboscis
 */
class ParticleTest extends ScreenBuilder {
  //パターンマッチを関数とし、
  //さらにそれを合成していけば？
  def requirements = Seq(
    AssetDescriptor[Texture]("data/particle.png")
  )
  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
    autoClearScreen = false
    val particleTex = assetManager.get[Texture]("data/particle.png")
    import TypedBatch._
    val batch = new TypedBatch[UVTrail](1000*10*2)
    val trails = new ArrayBuffer[UVTrail]
    //everything is working nice except the rendering engine.
    //i think i need to collect the rendering methods by scanning trees.
    //well, should i actually?
    preAlloc[PTrail](1000)
    preAlloc[PEntity](1000)
    preAlloc[PTrailHolder[PTrail]](1000)
    preAlloc[PInvoker](1000)
    val trailEntity = ()=>{
      val trail = manual[PTrail]
      val entity = auto[PEntity]
      val holder = auto[PTrailHolder[PTrail]]
      trails += trail
      holder.setup(trail,trails -= _)
      entity += holder
      entity
    }
    val explosion = (e:PEntity)=>{
      var i = 0
       while ( i < 50 ){
        val te = trailEntity()
        val rad = i/50f * PI2
        val v = 100f
        te.vel.set(sin(rad)*v,cos(rad)*v)
        e += te
        i += 1
      }
    }:Unit
    val pRoot = new PEntity
    //well, this is how it works
    pRoot.transform.translate(STAGE_WIDTH/2,STAGE_HEIGHT/2)
    pRoot += new PEmitter(()=>{
      val system = auto[PEntity]
      val trail = manual[PTrail]
      val mod = auto[PColorMod]
      mod.loop = true
      mod.interpolation = f => 1f - Math.abs(1f-2f*f)
      mod.duration = 0.8f
      mod.start.set(Color.BLUE)
      mod.end.set(Color.CYAN)
      trails += trail
      system += auto[PTrailHolder[PTrail]].setup(trail,trails -= _)
      system += mod
      system.vel.set(random(-1f,1f)*300,random(-1f,1f)*300)
      system += auto[PInvoker].setTarget(explosion)
      system
    })
    pRoot += new PRotation
    pRoot += new PGravity
    //pRoot += new Absorber(new Rectangle(-10,-10,20,20))

    override def render(delta: Float){
      clearScreen()
      pRoot.update(delta)
      pRoot.calculateTransformHierarchy()
      super.render(delta)

      Gdx.gl.glEnable(GL20.GL_TEXTURE_2D)
      Gdx.gl.glEnable(GL20.GL_BLEND)
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
      particleTex.bind(0)
      batch.combined.set(stage.getCamera.combined)
      batch.begin()
      trails foreach batch.draw
      batch.end()
    }
  }
}
class PTrail extends UVTrail(10)

class PTrailHolder[T<:UVTrail] private (var trail:T,var remover:T => Unit) extends PModifier{
  val tmp = new Vector2
  private implicit var trailPool : Pool[T] = null
  import PoolOps._
  def setup(trail:T,remover:T=>Unit)(implicit trailPool:Pool[T]):this.type = {
    this.trail = trail
    this.remover = remover
    this.trailPool = trailPool
    this
  }
  def this() = this(null.asInstanceOf[T],null)
  override def onWorldTransform(world: Matrix3): Unit = {
    super.onWorldTransform(world)
    world.getTranslation(tmp)
    trail.addTrail(tmp.x,tmp.y)
  }
  override def onUpdate(entity: PEntity, delta: Float): Unit = {
    super.onUpdate(entity, delta)
    trail.color.set(entity.color)
  }

  override def onDispose(entity: PEntity): Unit = {
    super.onDispose(entity)
    log("disposed")
    remover(trail)
    trail.free
  }

  override def reset(): Unit = {
    super.reset()
    log("reset")
    trail = null.asInstanceOf[T]
    remover = null
    trailPool = null
  }
}
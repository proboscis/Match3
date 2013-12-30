package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.badlogic.gdx.math.Vector2
import com.glyph.scala.lib.libgdx.gl._
import com.badlogic.gdx.graphics.VertexAttributes
import scalaz.{Failure, Success}
import scalaz.Success
import scalaz.Failure
import scala.Some

/**
 * @author proboscis
 */
class ParticleTest extends ScreenBuilder {
  //パターンマッチを関数とし、
  //さらにそれを合成していけば？
  def requiredAssets: Set[(Class[_], Seq[String])] = Set()

  def create(assetManager: AssetManager): Screen = new ConfiguredScreen {
    autoClearScreen = false
    trait UVTrailAttributes extends VertexAttributeHolder{
      def attributes: VertexAttributes = UVTrail.ATTRIBUTES
    }
    implicit object UVTrailDrawable extends DrawableStrip[UVTrail,UVTrailAttributes]{
      def vertices(tgt: UVTrail): Array[Float] = tgt.meshVertices
      def length(tgt: UVTrail): Int = tgt.count
    }

    val handler = ShaderHandler("shader/rotate2.vert", "shader/default.frag")
    val batch = handler.shader.map(_.map(new TypedStripBatch(1000*10*2,new UVTrailAttributes{},_)))

    override def render(delta: Float){
      clearScreen()
      super.render(delta)
      if(batch().isDefined){
        val b = batch().get
        b.begin()

        b.end()
      }
    }
  }
}

trait Transform {
  val p = new Vector2
  //position
  val v = new Vector2
  //velocity
  val a = new Vector2
  //acceleration
  var d = 0f

  //direction
  def reset() {
    p.set(0, 0)
    v.set(0, 0)
    a.set(0, 0)
    d = 0f
  }
}

trait Dynamic {
  def update(delta: Float)
}
trait NeedSystem {
  def system:ParticleSystem
}
trait Particle extends Transform with Dynamic with NeedSystem
trait Emitter extends Transform with Dynamic with NeedSystem
trait FunctionalEmitter extends Emitter{

}
//pattern!!!
trait ParticleSystem {
  val dynamics = new com.badlogic.gdx.utils.Array[Dynamic]()

  def update(delta: Float) {
    val it = dynamics.iterator()
    while (it.hasNext) {
      val d = it.next()
      d.update(delta)
    }
  }
}



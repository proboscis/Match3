package testing

import com.glyph.java.particle.{ParticlePool, SpriteParticle}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, MathUtils}
import com.glyph.scala.lib.libgdx.particle.Emission
import com.glyph.scala.lib.libgdx.actor.ReactiveSize
import com.glyph.scala.lib.util.reactive

/**
 * @author glyph
 */
object ParticleTest extends UITest {
  def screen: DebugScreen = new DebugScreen {
    AM.create()
    AM.instance().load("data/particle.png", classOf[Texture])
    AM.instance().finishLoading()
    override val backgroundColor: Color = Color.CYAN
    val tgt = new Actor with ReactiveSize

    import reactive._

    val emission = new Emission(tgt.rX ~ tgt.rY ~ tgt.rWidth ~ tgt.rHeight map {
      case x ~ y ~ width ~ height => new Rectangle(x, y, width, height)
    }, pool, 1f,0.2f,100)
    root.add(emission).size(0, 0).fill
    root.add(tgt).size(100, 100).fill
    root.layout()
    1 to 1000 foreach {
      _ => emission.+=(pool.obtain)
    }
    emission.particles foreach {
      p =>
        p.init(region)
        p.setSize(50, 50)
        import MathUtils._
        p.setPosition(random(0, root.getWidth), random(0, root.getHeight))
        p.getVelocity.set(random(0, 100), random(0, 100))
    }
  }

  lazy val texture: Texture = AM.instance().get("data/particle.png", classOf[Texture])
  lazy val region = new TextureRegion(texture)
  val pool = new ParticlePool(classOf[SpriteParticle], 1000)


}

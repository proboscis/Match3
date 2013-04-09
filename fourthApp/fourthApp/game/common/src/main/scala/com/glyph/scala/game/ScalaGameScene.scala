package com.glyph.scala.game

import adapter.RendererAdapter
import com.glyph.game.GameScene
import com.glyph.scala.lib.entity_component_system.Entity
import component.{DungeonActor, Transform}
import renderer.{SimpleRenderer, RendererComponent}
import com.glyph.scala.system.GameSystem
import com.glyph.libgdx.surface.drawable.SurfaceDrawable
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.surface.GameRenderer
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.glyph.scala.lib.entity_component_system.math.Vec2
import com.glyph.libgdx.Engine

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/03
 * Time: 0:06
 * To change this template use File | Settings | File Templates.
 */
class ScalaGameScene(x: Int, y: Int) extends GameScene(x, y) {
  val game = new GameContext
  val systems = List[GameSystem]()
  /**
   * initializer
   */
  (() => {
    game.entityContainer.addAdapter[RendererAdapter]
    for (i <- 1 to 1000) {
      val e = new Entity
      e.register({
        val t = new Transform
        t.position.set(Vec2.random.mul(MathUtils.random(Engine.VIRTUAL_WIDTH)))
        t
      })
      e.register({
        val r = new RendererComponent
        r.setRenderer(new SimpleRenderer)
        r
      })
      e.register(new DungeonActor)
      e.initialize()
      game.entityContainer.addEntity(e)
    }
    game.eventManager += callback
    def callback(i:Int)={
      true
    }

    /**
     * render system
     */
    mGameSurface.add(new SurfaceDrawable {
      val gameRenderer = new GameRenderer(game.entityContainer)
      def draw(batch: SpriteBatch, parentAlpha: Float) {
        gameRenderer.draw(batch,parentAlpha)
      }
    })
  })()

  override def render(delta: Float) {
    super.render(delta)
    systems.foreach(_.update(delta,game.entityContainer))
  }
}

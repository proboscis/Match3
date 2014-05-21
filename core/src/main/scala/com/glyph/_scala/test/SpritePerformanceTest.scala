package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.scenes.scene2d.{Action, Group, Actor}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Batch}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import scalaz.Scalaz
import Scalaz._
import com.glyph._scala.game.builders.Builders
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.libgdx.actor.ui.{RingArray, Graph}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */
class SpritePerformanceTest extends ConfiguredScreen {
  val flare: Texture = "data/flare.png"

  import Actions._

  override def createBatch: Batch = new SpriteBatch(5000)

  val skin = Builders.flat.forceCreate(new AssetManager())
  val number = new Label("???", skin) <| root.add
  val benches = (100 :: 1000 :: 2000 :: 3000 :: 5000 :: 10000 :: Nil).map(new SpriteBench(_, flare))
  val shapeRenderer = new ShapeRenderer()
  val fpsLog = new RingArray(60*5)
  val fpsGraph = new Graph(fpsLog,shapeRenderer,0,100) <| (root.add(_).fill.expand)
  def run(f: (Actor, Float) => Boolean) = new Action {
    def act(p1: Float): Boolean = f(actor, p1)
  }

  benches.map {
    bench => sequence(
      run {
        case (actor, et) => {
          actor.asInstanceOf[Group].addActor(bench)
          number.setText(bench.n + "")
          true
        }
      },
      delay(5),
      removeActor(bench))
  } |> (sequence(_: _*)) |> root.addAction

  override def render(delta: Float): Unit = {
    super.render(delta)
    fpsLog += Gdx.app.getGraphics.getFramesPerSecond
  }
}

class SpriteBench(val n: Int, tex: Texture) extends Group {
  override def draw(batch: Batch, parentAlpha: Float) {
    var i = 0
    while (i < n) {
      batch.draw(tex, 0, 0,100,100)
      i += 1
    }
  }
}
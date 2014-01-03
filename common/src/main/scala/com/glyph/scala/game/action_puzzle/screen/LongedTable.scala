package com.glyph.scala.game.action_puzzle.screen

import com.glyph.scala.game.action_puzzle._
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.lib.libgdx.actor.SpriteActor
import scala.reflect.ClassTag
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.blend.AdditiveBlend

class LongedGame {
  val puzzle = new ActionPuzzle[Int](8, 8, () => MathUtils.random(5), (_: Int) == (_: Int))

  def score: Int = 100
}

class Resource(implicit assets: AssetManager) {
  val roundRect: Texture = "data/round_rect.png".fromAssets
  val particle: Texture = "data/particle.png".fromAssets
}

class LongedTable(val assets: AssetManager) {

  import APViewTable._

  val resource = new Resource()(assets)
  val game = new LongedGame
  val main = new APView[Int, SpriteActor](game.puzzle)(textured(resource.roundRect), ClassTag(classOf[SpriteActor]))
    with Scoring[Int, SpriteActor]
    with Trailed[Int, SpriteActor]
    with AdditiveBlend{
    def score: Int = game.score

    def texture: Texture = resource.particle
  }
}

object APViewTable {
  def textured(texture: Texture) = new WithTextureRegion(new TextureRegion(texture))
}
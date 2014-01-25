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

class LongedTable(particleTex:Texture,roundTex:Texture) {

  import APViewTable._
  val game = new LongedGame
  val main = new APView[Int, SpriteActor](game.puzzle)(textured(roundTex), ClassTag(classOf[SpriteActor]))
    with Scoring[Int, SpriteActor]
    with Trailed[Int, SpriteActor]
    with AdditiveBlend{
    def score: Int = game.score

    def texture: Texture = particleTex
  }
}

object APViewTable {
  def textured(texture: Texture) = new WithTextureRegion(new TextureRegion(texture))
}
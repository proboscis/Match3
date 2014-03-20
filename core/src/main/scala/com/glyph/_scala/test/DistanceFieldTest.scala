package com.glyph._scala.test

import com.glyph._scala.game.action_puzzle.view.animated.LazyAssets
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph._scala.lib.libgdx.gl.ShaderUtil
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.game.Glyphs._
import com.glyph._scala.game.builders.Builders
import com.glyph._scala.lib.libgdx.Builder
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.{Batch, BitmapFont, TextureRegion}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
class DistanceFieldTest extends MockTransition with LazyAssets {
  val shader = ShaderUtil.load("shader/dist.vert", "shader/dist.frag")
  val test = shader.map(_.map(_.map(sp=>Builder[Texture]("data/dummy.png").map(tex=>new Actor{
  }))))
  val screen =
      Builder[Texture]("font/quicksand.png").map{
      tex =>
        tex.setFilter(TextureFilter.Linear,TextureFilter.Linear)
        val font = new BitmapFont(Gdx.files.internal("font/quicksand.fnt"),tex,false)
        println("created font!")
        new Actor{
          override def draw(batch: Batch, parentAlpha: Float): Unit = {
            super.draw(batch, parentAlpha)
            font.draw(batch,"hello",10,20)

            println("hello?")
          }
        }
    }
  override def graph: AnimatedManager.AnimatedGraph = super.graph
  manager.start(test,Map(),holder.push)
}
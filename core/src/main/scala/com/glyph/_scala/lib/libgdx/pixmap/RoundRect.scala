package com.glyph._scala.lib.libgdx.pixmap

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.{Texture, Color, Pixmap}
import com.glyph._scala.test.MockTransition
import com.glyph._scala.lib.libgdx.actor.transition.{AnimatedConstructor, DefaultExtractors}
import com.glyph._scala.game.action_puzzle.view.animated.LazyAssets
import com.glyph._scala.lib.libgdx.actor.AnimatedTable
import com.badlogic.gdx.scenes.scene2d.ui.Image

/**
 * @author glyph
 */
object RoundRect {
  def apply(arc:Int):NinePatch={
    val pixmap = new Pixmap(arc*2,arc*2,Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.WHITE)
    pixmap.fillCircle(arc,arc,arc)
    val texture = new Texture(pixmap)
    pixmap.dispose()
    new NinePatch(texture,arc-1,arc-1,arc-1,arc-1)
  }
}

class RoundRectTest extends MockTransition with LazyAssets{
  import scalaz._
  import Scalaz._
  val roundTest = AnimatedTable(_.fill.expand.space(20f).pad(20f),new Image(RoundRect(20))) |> AnimatedConstructor.apply
  manager.start(roundTest,Map(),holder.push)
}
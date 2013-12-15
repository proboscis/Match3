package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.g2d.{BitmapFontCache, SpriteBatch}
import com.glyph.scala.lib.libgdx.actor._
import com.glyph.scala.game.puzzle.model.cards._
import com.badlogic.gdx.scenes.scene2d.ui.{Image, WidgetGroup, Label, Table}
import com.glyph.scala.lib.util.reactive.{Varying, Reactor}
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.glyph.scala.game.puzzle.model.cards.ThunderCost
import com.glyph.scala.game.puzzle.model.cards.Thunder
import com.glyph.scala.game.puzzle.model.cards.WaterCost
import com.glyph.scala.game.puzzle.model.cards.Water
import com.glyph.scala.game.puzzle.model.cards.FireCost
import com.glyph.scala.game.puzzle.model.cards.Fire
import com.glyph.scala.lib.util.reactive
import com.glyph.scala.lib.libgdx.TextureUtil
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.widgets.Layered

/**
 * @author glyph
 */
case class CardToken[T](assets:AssetManager,card: Card[T]#PlayableCard, w: Float, h: Float, condition: Varying[Boolean])
  extends WidgetGroup with Layered
  with ExplosionFadeout
  with Reactor {

  import CardToken._

  val root = new Table
  val src = card.source
  type :=>[P, R] = PartialFunction[P, R]
  List[Cost[_] :=> Int](
  {
    case ThunderCost(Thunder(c)) => c
  }, {
    case WaterCost(Water(c)) => c
  }, {
    case FireCost(Fire(c)) => c
  }) map {
    pf => src.costs.collect(pf).sum
  } map {
    cost => new Label(cost + "", skin(assets))
  } foreach {
    label => label.setWrap(true)
      label.setFontScale(0.5f)
      root.add(label).expand.fillX.bottom()
  }
  val img = new Image(texture(assets))
  setSize(w, h)
  setOrigin(w / 2, h / 2)

  import reactive.~

  val reactor = reactVar(card.playable ~ condition) {
    case p ~ c =>
      if (p && c) {
        setColor(Color.LIGHT_GRAY)
        setTouchable(Touchable.enabled)
        img.setColor(getColor)
      } else {
        setColor(Color.BLACK)
        setTouchable(Touchable.disabled)
        img.setColor(getColor)
      }
  }
  addActor(img)
  addActor(root)
  //root.debug()
  val glyph = mapping.getOrElse(card.source.getClass.getSimpleName.charAt(0), mapping('?'))(assets)

  def dispose() {
    reactor.unSubscribe()
  }

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    val b = glyph.getBounds
    glyph.setPosition(getX + (getWidth - b.width) / 2, getY - (getHeight - b.height) / 3f + getHeight)
    glyph.setColor(Color.WHITE)
    glyph.draw(batch, getColor.a * parentAlpha)
  }
}

object CardToken {
  //val yggdrasil = new BitmapFont(Gdx.files.internal("font/yggdrasil.fnt"), false)
  val keys = "ABCDEFGHIJKLMNOPQRSTU?@".toCharArray

  def random(assets:AssetManager): BitmapFontCache = {
    mapping(keys(MathUtils.random(keys.length - 1)))(assets)
  }

  val mapping = Map(keys map {
    k =>
      val f =(assets:AssetManager)=>{
        val cache = new BitmapFontCache(yggdrasilFont(assets))
        cache.setText("" + k, 0f, 0f)
        cache
      }
      k -> f
  }: _*)

  def texture(assets:AssetManager) = TextureUtil.dummy(assets)
}


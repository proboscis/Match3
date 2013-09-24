package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.g2d.{TextureRegion, BitmapFontCache, SpriteBatch, Sprite}
import com.glyph.scala.lib.libgdx.actor.{Layered, ObsTouchable, ExplosionFadeout, OldDrawSprite}
import com.glyph.scala.game.puzzle.model.cards._
import com.badlogic.gdx.scenes.scene2d.ui.{Image, WidgetGroup, Label, Table}
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.util.reactive.Reactor
import com.badlogic.gdx.scenes.scene2d.Touchable

/**
 * @author glyph
 */
case class CardToken(card: Card#PlayableCard, w: Float, h: Float)
  extends WidgetGroup with Layered
  with ExplosionFadeout with ObsTouchable
  with Reactor{
  import CardToken._
  val root = new Table
  val src = card.source
  val labels = {
    (src.costs collect{
      case ThunderCost(Thunder(cost)) =>cost
    } sum) ::
      (src.costs collect{
        case WaterCost(Water(cost)) => cost
      } sum) ::
      (src.costs collect{
        case FireCost(Fire(cost)) => cost
      } sum) ::Nil
  } map{cost => new Label(cost+"",skin)}
  val img = new Image(texture)
  setSize(w, h)
  setOrigin(w / 2, h / 2)
  labels foreach{
    label => label.setWrap(true)
      label.setFontScale(0.5f)
      root.add(label).expand.fillX.bottom()
  }
  reactVar(card.playable){
    if(_){
      setColor(Color.LIGHT_GRAY)
      setTouchable(Touchable.enabled)
      img.setColor(getColor)
    }else{
      setColor(Color.BLACK)
      setTouchable(Touchable.disabled)
      img.setColor(getColor)
    }
  }
  addActor(img)
  addActor(root)
  //root.debug()
  val glyph = mapping.getOrElse(card.source.getClass.getSimpleName.charAt(0),mapping('?'))

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
  val keys = "YGGDRASIL?M".toCharArray

  def random(): BitmapFontCache = {
    mapping(keys(MathUtils.random(keys.length - 1)))
  }

  val mapping = Map(keys map {
    k =>
      val cache = new BitmapFontCache(yggdrasilFont)
      cache.setText("" + k, 0f, 0f)
      k -> cache
  }: _*)
  val image = new Pixmap(128, 128, Pixmap.Format.RGBA8888)
  image.setColor(Color.WHITE)
  image.fillRectangle(0, 0, image.getWidth, image.getHeight)
  val texture = new Texture(image)
}


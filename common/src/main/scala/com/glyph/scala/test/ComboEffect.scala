package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.ui.{Window, Label, Skin}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph.scala.lib.libgdx.actor.{SBDrawableGdx, SpriteBatchRenderer, SpriteActor, Tasking}
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph
import com.badlogic.gdx.graphics.g2d.{TextureRegion, BitmapFont, Sprite}
import scalaz._
import Scalaz._
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.scenes.scene2d.utils.{TextureRegionDrawable, SpriteDrawable, Drawable}

/**
 * @author glyph
 */
class ComboEffect extends ConfiguredScreen{
  backgroundColor = Color.BLACK
  val group = new Group with Tasking with SpriteBatchRenderer
  val skin = new Skin(Gdx.files.internal("skin/default.json"))
  val texture = new Texture(Gdx.files.internal("data/dummy.png"))
  val generator = new FreeTypeFontGenerator(Gdx.files.internal("font/corbert.ttf"))
  val font = generator.generateFont(80)
  val labelStyle = new Label.LabelStyle(font,Color.LIGHT_GRAY)
  skin.add("default",labelStyle)
  val windowStyle = new WindowStyle(font,Color.DARK_GRAY,new TextureRegionDrawable(new TextureRegion(texture)))
  skin.add("default",windowStyle)
  def fontRegions(font:BitmapFont)(characters:String):Char Map TextureRegion = (characters.toCharArray map {
    c =>
      val glyph = font.getData.getGlyph(c)
      val texture = font.getRegion.getTexture
      import glyph._
      c -> new TextureRegion(texture,srcX,srcY,width,height)
  }).toMap
  def placeSprites(seq:Seq[Sprite]){
    var x = 0f
    seq foreach{
      s => s.setX(x);x += s.getWidth
    }
  }
  //import SpriteBatchRenderer._
  import SBDrawableGdx._
  root.add(new Label("Testing!\n0123456789",skin)).row
  root.add(group).fill.expand
  val regions = fontRegions(font)("0123456789")
  val sprites = "0123456789" map regions map (new Sprite(_))
  sprites |> placeSprites
  group.addDrawable(sprites)
  root.debug()
  val window = new Window("test",skin)
  window.setSize(100,100)
  window.setTitle("window")
  window.add
  stage.addActor(window)
}

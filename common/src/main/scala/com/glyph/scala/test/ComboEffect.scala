package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph.scala.lib.libgdx.actor.{SBDrawable, SBDrawableGdx, SpriteBatchRenderer, Tasking}
import com.badlogic.gdx.graphics.g2d.{TextureRegion, BitmapFont, Sprite}
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable

/**
 * @author glyph
 */
class ComboEffect extends ConfiguredScreen {
  backgroundColor = Color.BLACK
  val group = new Group with Tasking with SpriteBatchRenderer
  val skin = new Skin(Gdx.files.internal("skin/default.json"))
  val texture = new Texture(Gdx.files.internal("data/dummy.png"))
  val generator = new FreeTypeFontGenerator(Gdx.files.internal("font/corbert.ttf"))
  val font = generator.generateFont(30)
  val labelStyle = new Label.LabelStyle(font, Color.WHITE)
  skin.add("default", labelStyle)
  val dummyDrawable = new SpriteDrawable(new Sprite(texture))
  dummyDrawable.getSprite.setColor(Color.DARK_GRAY)
  val windowStyle = new WindowStyle(font, Color.WHITE, dummyDrawable)
  skin.add("default", windowStyle)

  def fontRegions(font: BitmapFont)(characters: String): Char Map TextureRegion = (characters.toCharArray map {
    c =>
      val glyph = font.getData.getGlyph(c)
      val texture = font.getRegion.getTexture
      import glyph._
      c -> new TextureRegion(texture, srcX, srcY, width, height)
  }).toMap

  def placeSprites(seq: Seq[Sprite]) {
    var x = 0f
    seq foreach {
      s => s.setX(x); x += s.getWidth
    }
  }

  //import SpriteBatchRenderer._

  import SBDrawableGdx._

  root.add(new Label("Testing!\n0123456789", skin)).row
  root.add(group).fill.expand
  val regions = fontRegions(font)("0123456789")
  val sprites:Seq[Sprite] = "0123456789" map regions map (new Sprite(_))
  sprites |> placeSprites
  implicit val spritesCls = classOf[Seq[Sprite]]
  group.addDrawable(sprites)
  root.debug()
  val window = new Window("window", skin)
  window.setMovable(true)
  window.padTop(30)
  window.getButtonTable.debug()
  window.getButtonTable.add(new Label("X", skin)).height(100)
  window.setPosition(0, 0)
  //window.setSize(300,300)
  window.add.height(50).colspan(2).row
  window.add.expand.height(100)
  window.add.expand
  window.debug()
  window.pack()
  stage.addActor(window)
  //Gdx.input.setInputProcessor(stage)
  val dialog = new Dialog("dialog", skin)
  dialog.size(300, 300)
  dialog.padTop(30)
  dialog.debug()
  dialog.pack()

  //dialog.show(stage)

  override def render(delta: Float) {
    super.render(delta)
    Table.drawDebug(stage)
  }
}

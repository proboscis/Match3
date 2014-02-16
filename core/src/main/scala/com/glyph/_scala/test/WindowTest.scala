package com.glyph._scala.test

import com.glyph._scala.lib.util.screen.GlyphScreen
import com.badlogic.gdx.scenes.scene2d.{Group, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Window, Label, Skin, Table}
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.libgdx.actor.{SpriteBatchRenderer, Tasking}
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle

/**
 * @author proboscis
 */
class WindowTest extends GlyphScreen{
  val stage = new Stage()
  Gdx.input.setInputProcessor(stage)
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

  val window = new Window("window",skin)
  window.setMovable(true)
  window.padTop(30)
  window.getButtonTable.debug()
  window.getButtonTable.add(new Label("X",skin)).height(100)
  window.setPosition(0,0)
  //window.setSize(300,300)
  window.add.height(50).colspan(2).row
  window.add.expand.height(100)
  window.add.expand
  window.debug()
  window.pack()
  stage.addActor(window)
  override def render(delta: Float){
    super.render(delta)
    stage.act(delta)
    stage.draw()
    Table.drawDebug(stage)
  }
}

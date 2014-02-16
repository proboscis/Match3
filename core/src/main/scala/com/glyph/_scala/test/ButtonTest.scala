package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.scenes.scene2d.ui.{TextButton, Button, Skin}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.lib.libgdx.skin.FlatSkin
import com.glyph._scala.game.action_puzzle.ColorTheme
import com.badlogic.gdx.graphics.g2d.Sprite

/**
 * @author glyph
 */
class ButtonTest extends ConfiguredScreen {

  import scalaz._
  import Scalaz._

  val skin = new Skin(Gdx.files.internal("skin/flat/flat.json"))
  val font = skin.getFont("default-font")
  val texture = new Texture(Gdx.files.internal("data/dummy.png"))
  val flat = new FlatSkin(
    ColorTheme.varyingColorMap(),
    c => new SpriteDrawable(new Sprite(texture) <| (_.setColor(c))),
    font
  )
  flat.textButtonStyles.toSeq.sortBy(_._1).map{
    case(name,style) => new TextButton(name,style)
  }.zipWithIndex.foreach{
    case (button,i) =>
      root.add(button).fill.expand
      if(i%2 == 0) root.row()
  }
}
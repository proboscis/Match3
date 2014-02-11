package com.glyph._scala.lib.libgdx.skin

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
class FlatSkin(val colors: Map[String, Color], tint: Color => Drawable, font: BitmapFont) extends Skin with Logging{
  private implicit def colorToDrawable(c: Color): Drawable = tint(c)

  def up(c: Color): Color = c.cpy.add(0.1f, 0.1f, 0.1f, 0.1f)

  def down(c: Color): Color = c.cpy.sub(0.1f, 0.1f, 0.1f, 0.1f)

  val drawables = colors mapValues tint
  val buttonStyles = colors.mapValues(buttonStyle)
  val textButtonStyles = colors.mapValues(textButtonStyle)
  val labelStyles = colors mapValues labelStyle
  def textButtonStyle(c: Color): TextButtonStyle = {
    val ts = new TextButtonStyle()
    ts.font = font
    ts.over = up(c)
    ts.down = down(c)
    ts.up = c
    ts
  }

  def buttonStyle(c: Color): ButtonStyle = {
    val bs = new ButtonStyle()
    bs.over = up(c)
    bs.down = down(c)
    bs.up = c
    bs
  }

  def labelStyle(c: Color) = new LabelStyle(font, c)

  val defaults = labelStyle(Color.WHITE)::buttonStyles("carrot") :: textButtonStyles("carrot") :: Nil
  val defaultStyles = defaults map {
    style => "default"->style
  }
  val res = labelStyles.toSeq ++ buttonStyles.toSeq ++ textButtonStyles.toSeq ++ defaultStyles
  res foreach {
    case (name, style) => add(name, style)
  }
  drawables foreach{
    case (name,style)=>add(name,style,classOf[Drawable])
  }
}
package com.glyph._scala.lib.libgdx.skin

import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.utils.{SpriteDrawable, Drawable}
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.graphics.g2d.{Batch, Sprite, BitmapFont}
import com.badlogic.gdx.scenes.scene2d.ui.{ImageButton, Label, Table, Skin}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.glyph._scala.lib.util.Logging
import com.glyph._scala.game.action_puzzle.ColorTheme
import com.glyph._scala.game.builders.Builders
import com.glyph._scala.lib.libgdx.Builder
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.glyph._scala.lib.libgdx.drawable.{Tint, DrawableCopy}
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.glyph._scala.game.action_puzzle.view.animated.Change

/**
 * @author glyph
 */
class FlatSkin(val colors: Map[String, Color], tint: Drawable, font: BitmapFont) extends Skin with Logging{
  import FlatSkin._

  def up(c: Color): Color = c.cpy.add(0.1f, 0.1f, 0.1f, 0.1f)
  def down(c: Color): Color = c.cpy.sub(0.1f, 0.1f, 0.1f, 0.1f)
  implicit def colorToDrawable(c:Color) = FlatSkin.tint(tint,c)
  val drawables = colors mapValues colorToDrawable
  val buttonStyles = colors.mapValues(buttonStyle)
  val textButtonStyles = colors.mapValues(textButtonStyle)
  val inversedTextButtonStyles = colors.mapKeys("inversed_"+_).mapValues(textButtonInversed)
  val labelStyles = colors mapValues labelStyle
  val scrollPaneStyles = colors mapValues scrollPaneStyle
  val listStyles = colors mapValues listStyle
  def textButtonStyle(c: Color): TextButtonStyle = {
    val ts = new TextButtonStyle()
    ts.font = font
    ts.over = up(c)
    ts.down = down(c)
    ts.up = c
    ts
  }
  def textButtonInversed(c:Color):TextButtonStyle = {
    val ts =  new TextButtonStyle()
    val whiteAlpha = new Color(1,1,1,0.9f)
    ts.font = font
    ts.fontColor = c
    ts.over = up(whiteAlpha)
    ts.down = down(whiteAlpha)
    ts.up = whiteAlpha
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
  def scrollPaneStyle(c:Color) = new ScrollPaneStyle(c,up(c),down(c),up(c),down(c))
  def listStyle(c:Color) = new ListStyle(font,Color.WHITE,Color.WHITE,down(c))
  val defaults =
    labelStyle(Color.WHITE)::
    buttonStyles("carrot") ::
      textButtonStyles("carrot") ::
      scrollPaneStyles("carrot")::
      listStyles("carrot") :: Nil
  val defaultStyles = defaults map {
    style => "default"->style
  }

  colors.toSeq ++
    inversedTextButtonStyles.toSeq ++
    labelStyles.toSeq ++
    buttonStyles.toSeq ++
    textButtonStyles.toSeq ++
    defaultStyles :+ ("default-font"->font) foreach {
    case (name, style) => add(name, style)
  }
  drawables foreach{
    case (name,style)=>add(name,style,classOf[Drawable])
  }
}
object FlatSkin{
  import scalaz._
  import Scalaz._
  def tint(drawable:Drawable,c: Color): Drawable = new DrawableCopy(drawable) with Tint <| (_.color.set(c))
  def default(font:BitmapFont,drawable:Drawable)=new FlatSkin(ColorTheme.varyingColorMap(),drawable,font)
  def lighter(c:Color) = c.cpy.add(0.2f,0.2f,0.2f,0.2f)
  def darker(c:Color) = c.cpy.sub(0.1f,0.1f,0.1f,0.1f)
  def createImageButtonStyle(drawable:Drawable,color:Color):ImageButtonStyle = {
    val style = new ImageButtonStyle()
    style.imageUp = tint(drawable,color)
    style.imageDown = tint(drawable,lighter(color))
    style
  }
  def createButtonStyle(drawable:Drawable,color:Color,light:Boolean):ButtonStyle = {
    val style = new ButtonStyle
    style.up   = drawable
    style.down = tint(drawable,light ? darker(color) | lighter(color))
    style
  }
  def createLightButtonStyle(drawable:Drawable,color:Color) = createButtonStyle(drawable,color,true)
  def createDarkButtonStyle(drawable:Drawable,color:Color) = createButtonStyle(drawable,color,false)
}
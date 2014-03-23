package com.glyph._scala.lib.libgdx.actor.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
trait FillX extends Label with Logging{
  def getTextWidth = getStyle.font.getMultiLineBounds(getText).width
  def calcFontScale(){
    val scale = getWidth/getTextWidth
    setFontScale(scale)
    err("setFontScale",scale)
  }
  override def setText(newText: CharSequence): Unit = {
    super.setText(newText)
    calcFontScale()
  }

  override def sizeChanged(): Unit = {
    super.sizeChanged()
    calcFontScale()
  }
}

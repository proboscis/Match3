package com.glyph.scala.lib.util.screen


/**
 * @author glyph
 */
trait GlyphScreen extends com.badlogic.gdx.Screen {

  def render(delta: Float) {}

  def resize(w: Int, h: Int) {}

  def show() {}

  def hide() {}

  def pause() {}

  def resume() {}

  def dispose() {}
}


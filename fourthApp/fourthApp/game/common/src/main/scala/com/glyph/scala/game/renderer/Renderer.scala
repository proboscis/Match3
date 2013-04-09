package com.glyph.scala.game.renderer

import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/08
 * Time: 0:34
 * To change this template use File | Settings | File Templates.
 */
abstract class Renderer{
  def render(batch:SpriteBatch,alpha:Float,component:RendererComponent)
}

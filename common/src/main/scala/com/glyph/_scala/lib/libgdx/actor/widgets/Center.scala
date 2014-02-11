package com.glyph._scala.lib.libgdx.actor.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table

/**
 * @author glyph
 */
case class Center(actor:Actor) extends Table{
  debug()
  add(actor)
}

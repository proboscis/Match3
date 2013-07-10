package com.glyph.scala.game.puzzle.view.panel

import com.glyph.scala.game.puzzle.model.panels.Panel
import com.glyph.scala.game.puzzle.model.panels.Panel.{Fire, Water, Thunder}
import com.glyph.scala.lib.libgdx.actor.OldDrawSprite
import com.badlogic.gdx.graphics.Color

/**
 * @author glyph
 */
class ElementToken(panel: Panel) extends PanelToken(panel) with OldDrawSprite {
  setColor(Color.valueOf(panel match {
    case _: Thunder => "ffcc00"
    case _: Water => "88e02e"
    case _: Fire => "ff8800"
    case _ => "000000"
  }))
}

object ElementToken {

}
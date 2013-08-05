package com.glyph.scala.game.puzzle.view.panel

import com.glyph.scala.lib.libgdx.actor.OldDrawSprite
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.game.puzzle.model.puzzle.Panel
import com.glyph.scala.game.puzzle.model.Element.{Water, Thunder, Fire}
import com.glyph.scala.game.puzzle.model.monsters.Monster

/**
 * @author glyph
 */
class ElementToken(panel: Panel) extends PanelToken(panel) with OldDrawSprite {
  setColor(Color.valueOf(panel match {
    case _:Fire => "ffcc00"
    case _:Water => "88e02e"
    case _:Thunder => "ff8800"
    case _:Monster => "000000"
  }))
}

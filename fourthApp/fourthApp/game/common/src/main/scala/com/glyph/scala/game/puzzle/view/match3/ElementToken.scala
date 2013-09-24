package com.glyph.scala.game.puzzle.view.match3

import com.glyph.scala.lib.libgdx.actor.OldDrawSprite
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.game.puzzle.model.match_puzzle.{Move, Life, Panel}
import com.glyph.scala.game.puzzle.model.Element.{Water, Thunder, Fire}
import com.glyph.scala.game.puzzle.model.monsters.Monster
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.json.RJSON

/**
 * @author glyph
 */
class ElementToken(panel: Panel) extends PanelToken(panel) with OldDrawSprite {
  import ElementToken._
  setColor(Color.valueOf({
    panel match {
      case _:Fire => FIRE().getOrElse("")
      case _:Water => WATER().getOrElse("")
      case _:Thunder => THUNDER().getOrElse("")
      case _:Monster => MONSTER().getOrElse("")
      case _:Life => LIFE().getOrElse("")
      case _:Move => MOVE().getOrElse("")
      case _ => "ffffff"
    }
  }))
}
object ElementToken{
  val scheme = RJSON(GdxFile("js/view/panelView.js"))
  val FIRE = scheme.fire.as[String]
  val WATER = scheme.water.as[String]
  val THUNDER = scheme.thunder.as[String]
  val MONSTER = scheme.monster.as[String]
  val LIFE = scheme.life.as[String]
  val MOVE = scheme.move.as[String]
}

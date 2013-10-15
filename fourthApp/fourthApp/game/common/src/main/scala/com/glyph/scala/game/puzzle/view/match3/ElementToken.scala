package com.glyph.scala.game.puzzle.view.match3

import com.badlogic.gdx.graphics.Color
import com.glyph.scala.game.puzzle.model.match_puzzle.Panel
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.util.reactive.Varying

/**
 * @author glyph
 */
class ElementToken(panel: Panel) extends PanelToken(panel)

object ColorTheme {
  val scheme = RJSON(GdxFile("js/colors.js").getString)

  implicit def json2Str(json: RJSON): Varying[Color] = json.as[String] map {
    str => Color.valueOf(str getOrElse "ffffff")
  }

  type VC = Varying[Color]
  val fire: VC = scheme.fire
  val water: VC = scheme.water
  val thunder: VC = scheme.thunder
  val monster: VC = scheme.monster
  val life: VC = scheme.life
  val move: VC = scheme.move
}


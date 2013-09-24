package com.glyph.scala.game.puzzle.view.chain

import com.glyph.scala.game.puzzle.model.chain_puzzle.ChainPanel
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.actor.{TouchSource, ExplosionFadeout, DrawSprite}
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils

/**
 * @author glyph
 */
class CPToken(val token:ChainPanel) extends Actor with DrawSprite with ExplosionFadeout with TouchSource{
  import CPToken._
  setColor(Color.valueOf(MathUtils.random(2) match{
    case 0 => FIRE().getOrElse("")
    case 1 => WATER().getOrElse("")
    case 2 => THUNDER().getOrElse("")
    case _ =>"ffffff"
  }))
}
object CPToken{
  val scheme = RJSON(GdxFile("js/view/panelView.js"))
  val FIRE = scheme.fire.as[String]
  val WATER = scheme.water.as[String]
  val THUNDER = scheme.thunder.as[String]
  val MONSTER = scheme.monster.as[String]
  val LIFE = scheme.life.as[String]
  val MOVE = scheme.move.as[String]
}

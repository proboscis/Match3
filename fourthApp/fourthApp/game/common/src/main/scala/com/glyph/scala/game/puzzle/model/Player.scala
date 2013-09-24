package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.reactive._
import scala.language.dynamics
import com.glyph.scala.game.puzzle.system.TurnProcessor
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.util.lifting.Clamp

/**
 * @author glyph
 */
class Player(file: RFile) extends Reactor with TurnProcessor {
  val json = RJSON(file)
  val maxHp = Var(20f)
  val hp = new Var(20f) with Clamp[Float].clamp(Var(0f), maxHp)
  val experience = Var(0f)
  val fireMana = new Divider(json.fireDiv.as[Float] map{_.getOrElse(0f)},1)
  val thunderMana = new Divider(json.thunderDiv.as[Float] map{_.getOrElse(0f)},1)
  val waterMana = new Divider(json.waterDiv.as[Float] map {_.getOrElse(0f)},1)
  val position = Var(1)
  reactVar(json.maxHp.as[Float]) {
    for (max <- _) {
      maxHp() = max
    }
  }
  reactVar(json.hp.as[Float]) {
    _.foreach {
      hp.update
    }
  }
  reactVar(json.position.as[Int]) {
    _.foreach {
      position.update
    }
  }
}
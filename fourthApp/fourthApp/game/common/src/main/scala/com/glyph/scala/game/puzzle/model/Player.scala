package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.reactive._
import scala.language.dynamics
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.util.lifting.Clamp

/**
 * @author glyph
 */
class Player(file: RFile) extends Reactor{
  val json = RJSON(file.map{_.right.getOrElse("")})
  val maxHp = Var(20f,"Player:maxHp")
  val hp = new Var(20f,"Player:hp") with Clamp[Float].clamp(Var(0f), maxHp)
  val experience = Var(0f)
  val fireMana = Var(1)//new Divider(json.fireDiv.as[Float] map{_.getOrElse(0f)},1)
  val thunderMana = Var(1)//new Divider(json.thunderDiv.as[Float] map{_.getOrElse(0f)},1)
  val waterMana = Var(1)//new Divider(json.waterDiv.as[Float] map {_.getOrElse(0f)},1)
  val position = Var(1,"Player:position")
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
  fireMana.debugReactive("fireMana")
  thunderMana.debugReactive("thunderMana")
  waterMana.debugReactive("waterMana")
}
package com.glyph.scala.game.puzzle.model

import com.glyph.scala.lib.util.reactive._
import scala.language.dynamics
import com.glyph.scala.lib.util.json.{RVJSON, JSON, RJSON}
import com.glyph.scala.lib.util.lifting.Clamp

/**
 * @author glyph
 */
class Player(file: RFile) extends Reactor{
  val json = RVJSON(file)
  //val json = file.map{_.map(script =>JSON(script))}
  //val json =  RJSON(file.map{_.right.getOrElse("")})
  val maxHp = Var(20f,"Player:maxHp")
  val hp = new Var(20f,"Player:hp") with Clamp[Float].clamp(Var(0f), maxHp)
  val experience = Var(0f)
  val fireMana = Var(1)//new Divider(json.fireDiv.as[Float] map{_.getOrElse(0f)},1)
  val thunderMana = Var(1)//new Divider(json.thunderDiv.as[Float] map{_.getOrElse(0f)},1)
  val waterMana = Var(1)//new Divider(json.waterDiv.as[Float] map {_.getOrElse(0f)},1)
  val position = Var(1,"Player:position")
  /*
  reactVar(json){
    for(obj <- _){
      for(max <- obj.maxHp.as[Float]){
        maxHp() = max
      }
      for(hp <- obj.hp.as[Float]){
        this.hp()= hp
      }
      for(pos <- obj.pos.as[Int]){
        position()=pos
      }
    }
  }
  */
  reactVar(json.maxHp.as[Float]){
    vnel =>println(vnel); vnel.foreach(maxHp.update)
  }
  reactVar(json.hp.as[Float]){
    vnel=>println(vnel);vnel.foreach(hp.update)
  }
  reactVar(json.position.as[Int]){
    vnel=> println(vnel);vnel.foreach(position.update)
  }
  fireMana.debugReactive("fireMana")
  thunderMana.debugReactive("thunderMana")
  waterMana.debugReactive("waterMana")
}
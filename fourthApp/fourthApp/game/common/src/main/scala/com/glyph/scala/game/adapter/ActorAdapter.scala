package com.glyph.scala.game.adapter

import com.glyph.scala.game.component.{GameActor}
import com.glyph.scala.lib.entity_component_system.{Entity, Adapter, Receptor}

/**
 */
class ActorAdapter(e:Entity) extends Adapter(e:Entity){
  @Receptor
  val actor:GameActor = null
}

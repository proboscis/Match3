package com.glyph.scala.lib.entity_game_system

import com.glyph.scala.lib.event.EventManager
import com.glyph.scala.lib.util.{TypeCheckedMap, ArrayMap}

/**
 * @author glyph
 */
class GameContext extends TypeCheckedMap[String,Any]{
  val eventManager = new EventManager
}

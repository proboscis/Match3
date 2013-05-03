package com.glyph.scala.game.component

import collection.mutable.ListBuffer

/**
 * @author glyph
 */
class Update {
  val updates = new ListBuffer[(Float)=>Unit]
}

package com.glyph.scala.game.puzzle.model.panels

import util.Random

/**
 * @author glyph
 */
class Panel{
}
object Panel{
  private val rand = new Random()
  private val seed = List(()=>{new Fire},()=>{new Water},()=>{new Thunder})
  case class Fire() extends Panel
  case class Water() extends Panel
  case class Thunder() extends Panel
  def random():Panel={
    seed(rand.nextInt(seed.size))()
  }
}

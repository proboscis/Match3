package com.glyph.scala.game.action_puzzle.view.animated

import com.glyph.scala.game.builders.Builders
import com.glyph.scala.game.action_puzzle.view.animated.AnimatedTable
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author proboscis
 */
class AnimatedPuzzleTable() {
}
object AnimatedPuzzleTable{
  import Builders._
  import scalaz._
  import Scalaz._

  val animated :Actor => AnimatedConstructor = actor=> info=> callbacks =>{
    val result = new AnimatedTable
    result.add(actor).fill.expand
    result
  }
}
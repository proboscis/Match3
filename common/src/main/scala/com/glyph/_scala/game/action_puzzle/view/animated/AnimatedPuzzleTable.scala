package com.glyph._scala.game.action_puzzle.view.animated

import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.libgdx.actor.AnimatedTable
object AnimatedPuzzleTable {
  val animated: Actor => AnimatedConstructor = actor => info => callbacks => {
    val result = new AnimatedTable
    result.add(actor).fill.expand
    result
  }
}
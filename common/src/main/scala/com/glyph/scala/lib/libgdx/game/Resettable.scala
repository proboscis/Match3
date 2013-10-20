package com.glyph.scala.lib.libgdx.game

import com.badlogic.gdx._

/**
 * @author glyph
 */
trait Resettable extends Game{
  def setScreenConstructor(f: => Screen) {
    Gdx.input.setInputProcessor(null)
    //val usedVars = Var.allVariables
    setScreen(f)
    val currentProcessor = Gdx.input.getInputProcessor
    val multiplexer = new InputMultiplexer()
    multiplexer.addProcessor(new InputAdapter {
      override def keyDown(keycode: Int): Boolean = keycode match {
        case Input.Keys.R => setScreenConstructor(f); true
        case _ => false
      }
    })
    multiplexer.addProcessor(currentProcessor)
    Gdx.input.setInputProcessor(multiplexer)
  }
}

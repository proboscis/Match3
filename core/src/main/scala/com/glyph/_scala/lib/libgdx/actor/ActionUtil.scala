package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.{Actor, Action}

/**
 * @author glyph
 */
trait ActionUtil extends Actor{
  def addActionWithCallback(a: Action)(func: =>Unit){
    import com.badlogic.gdx.scenes.scene2d.actions.Actions._
    super.addAction(sequence(a,run(new Runnable {
      def run() {
        func
      }
    })))
  }
}

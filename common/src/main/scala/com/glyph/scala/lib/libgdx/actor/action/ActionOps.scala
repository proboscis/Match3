package com.glyph.scala.lib.libgdx.actor.action

import com.badlogic.gdx.scenes.scene2d.actions.{Actions, RunnableAction}

/**
 * @author proboscis
 */
trait ActionOps {
  import com.badlogic.gdx.scenes.scene2d.actions.Actions._
  def run(f:()=>Unit):RunnableAction=Actions.run(new Runnable {
      def run() = f()
    })
}

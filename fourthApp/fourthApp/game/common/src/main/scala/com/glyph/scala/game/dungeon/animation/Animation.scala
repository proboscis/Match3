package com.glyph.scala.game.dungeon.animation

import com.glyph.scala.lib.util.update.Updatable
import com.glyph.scala.lib.util.callback.Callback

/**
 * @author glyph
 */
abstract class Animation(manager:AnimationManager) extends Updatable{
  lazy val onAnimationEnd = new Callback
  def start(){}
  def end(){
    manager.animationEnd(this)
    onAnimationEnd()
  }
}

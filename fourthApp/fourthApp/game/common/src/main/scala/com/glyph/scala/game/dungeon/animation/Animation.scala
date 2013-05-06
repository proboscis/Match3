package com.glyph.scala.game.dungeon.animation

import com.glyph.scala.lib.util.update.Updatable

/**
 * @author glyph
 */
abstract class Animation(manager:AnimationManager) extends Updatable{
  def start(){}
  def end(){
    manager.animationEnd(this)
  }
}

package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.math.Interpolation


/**
 * @author glyph
 */
trait ExplosionFadeout extends ActionUtil{
  //TODO traitで実装
  def explode(f: =>Unit){
    import com.badlogic.gdx.scenes.scene2d.actions.Actions._
    val fade = fadeOut(0.3f)
    val scale = scaleTo(1.5f,1.5f,0.3f)
    this.addActionWithCallback(sequence(parallel(fade,scale))){
      f
    }
  }
}

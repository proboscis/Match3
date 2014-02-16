package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Action


/**
 * @author glyph
 */
trait ExplosionFadeout extends ActionUtil {
  import com.badlogic.gdx.scenes.scene2d.actions.Actions._
  def explode(f: => Unit) {
    val fade = fadeOut(0.3f)
    val scale = scaleTo(1.5f, 1.5f, 0.3f)
    this.addActionWithCallback(sequence(parallel(fade, scale))) {
      f
    }
  }
}
object ExplosionFadeout{
  import com.badlogic.gdx.scenes.scene2d.actions.Actions._
  def apply():Action = parallel(fadeOut(0.3f),scaleTo(1.5f,1.5f,0.3f))
}

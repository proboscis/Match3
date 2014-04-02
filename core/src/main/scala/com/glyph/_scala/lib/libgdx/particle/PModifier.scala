package com.glyph._scala.lib.libgdx.particle

import com.glyph._scala.lib.util.pool.Poolable
import com.badlogic.gdx.math.Matrix3

/**
 * @author glyph
 */
trait PModifier extends Poolable{
  def onUpdate(entity:PEntity,delta:Float){}
  def onDispose(entity:PEntity){}
  def onWorldTransform(world:Matrix3){}
  def reset(){}
}

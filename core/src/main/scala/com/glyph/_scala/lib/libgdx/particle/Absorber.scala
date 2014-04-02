package com.glyph._scala.lib.libgdx.particle

import com.badlogic.gdx.math.{Vector2, Rectangle}

/**
 * absorbs systems to the specified field
 * @param field
 */
class Absorber(var field:Rectangle) extends PModifier{
  val tmp = new Vector2
  def this() = this(null)
  def setup(field:Rectangle){
    this.field = field
  }
  override def onUpdate(system: PEntity, delta: Float): Unit ={
    super.onUpdate(system, delta)
    val children = system.children
    val size = children.size
    var i = 0
    while ( i < size){
      val child = children(i)
      child.transform.getTranslation(tmp)
      if(field.contains(tmp)){
        system -= child
      }
      i += 1
    }
  }
}

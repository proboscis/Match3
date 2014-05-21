package com.glyph._scala.lib.ecs.script

import com.badlogic.gdx.math.{Vector2, Rectangle}
import com.glyph._scala.lib.ecs.component.Transform
import Transform._
import com.glyph._scala.lib.ecs.Entity

/**
 * callbacks when any entity enters to the specified area
 * does nothing else.
 * @author glyph
 */
class AreaSensor(val area:Rectangle,var callback:Entity=>Unit) extends Script {
  def this() = this(new Rectangle,null)
  val tmp = new Vector2
  override def update(delta: Float): Unit = {
    super.update(delta)
    val children = entity.children
    val it = children.begin()
    while(it.hasNext){
      val e = it.next()
      val trans = e.component[Transform]
      if(trans != null && area.contains(trans.matrix.getTranslation(tmp))){
        if(callback != null) callback(e)
      }
    }
    children.end()
  }

  override def reset(): Unit = {
    super.reset()
    callback = null
  }
}

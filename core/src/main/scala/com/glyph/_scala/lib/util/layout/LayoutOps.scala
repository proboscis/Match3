package com.glyph._scala.lib.util.layout

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import scala.collection.JavaConversions._
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.libgdx.actor.ActorOps
import ActorOps._
import com.glyph._scala.lib.util.layout.LayoutOps.TableLayoutOpsImpl

/**
 * @author glyph
 */
trait LayoutOps{
  implicit def rectangleIsLayout(rect:Rectangle):Array[Float] = Array(rect.x,rect.y,rect.width,rect.height)
  implicit def tableIsTableLayoutOpsImpl(table:Table):TableLayoutOpsImpl = new TableLayoutOpsImpl(table)
}
//I'm not gonna use this!
object LayoutOps {
  implicit class TableLayoutOpsImpl(val table:Table) extends AnyVal{
    def layouts:Seq[Rectangle] = {
      table.getCells.collect{
        case cell if cell.getWidget != null => cell.getWidget
      }.collect {
        case t:Table =>t.layouts.map(rect =>{
          rect.x += table.getX
          rect.y += table.getY
          rect
        })
        case actor:Actor => actor.bounds::Nil
      }.flatten
    }
  }
  type Layout = Array[Float]
  /**
   * Layout destinations!
   */
  type Layouts = Seq[Layout]
}

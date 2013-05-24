package com.glyph.scala.lib.util.tile

import com.glyph.scala.lib.util.json.JSON._
import com.glyph.scala.lib.util.json.ScalaJSON

/**
 * @author glyph
 */
class TileMap(
  val width:Int,
  val height:Int,
  val tileWidth:Int,
  val tileHeight:Int,
  val layers:Seq[Layer]
               ){
  override def toString: String = {
    "w:"+width+"h:"+height+"tw:"+tileWidth+"th"+tileHeight+"layers:"+layers
  }
}
class Layer(
  val width:Int,
  val height:Int,
  val data:Seq[Int]
){
  override def toString: String = {
    "w:"+width+"h:"+height+"data:"+data
  }
}

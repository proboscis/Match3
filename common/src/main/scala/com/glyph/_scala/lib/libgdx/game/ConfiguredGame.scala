package com.glyph._scala.lib.libgdx.game


/**
 * @author glyph
 */
trait ConfiguredGame{
  def deskTopConfig:ApplicationConfig = ApplicationConfig(1920/3*9d/16d toInt,1920/3)
}
case class ApplicationConfig(width:Int,height:Int)

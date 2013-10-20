package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
abstract class Delay extends TimedTask{}
object Delay{
  //TODO 戦闘を廃止してパズルライクにする。
  //TODO カードはモンスターパネルを特殊な消し方をすることで取得する
  def apply(d:Float):Delay={
    new Delay {
      var duration: Float = d
    }
  }
  def apply():Delay={
    new Delay {
      var duration: Float = 0
    }
  }
}

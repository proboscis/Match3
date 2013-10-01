package com.glyph.scala.game.puzzle.model

import com.glyph.scala.game.puzzle.model.match_puzzle.{Life, Panel}
import com.glyph.scala.game.puzzle.model.monsters.{Worm, Slime, Monster}
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.puzzle.model.Element.{Water, Thunder, Fire}

/**
 * @author glyph
 */
class Dungeon {
  val goal = 10
  def getPanel(floor:Int):Panel = {
    import MathUtils.random
    random(0,6) match {
      case 0 => new Fire
      case 1 => new Thunder
      case 2 => new Water
      case 3|4|5 => Vector(appearance.collect {
        case (seed, floors) if floors.contains(floor) => seed
      }.toSeq:_*).random()
      case 6 => new Life
    }
  }
  val appearance :Set[(()=>Monster,Set[Int])] = Set(
    (()=> new Slime,Set(0 to 5 :_*)),
    (()=> new Worm,Set(1 to 7 :_*))
  )

  implicit class RandSeq[T](seq:IndexedSeq[T]){
    def random:T = seq(MathUtils.random(0,seq.size-1))
  }
}

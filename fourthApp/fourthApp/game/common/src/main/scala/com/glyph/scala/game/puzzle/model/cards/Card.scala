package com.glyph.scala.game.puzzle.model.cards

import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.util.reactive.{Var, Varying}
import com.glyph.scala.lib.util.reactive
import scala.util.Random

/**
 * @author glyph
 */
trait Card[T] {
  self =>
  protected def applyImpl(controller: T)(cb: Option[CardResult] => Unit)
  def costs: Seq[Cost[T]]

  def createPlayable(controller: T): PlayableCard = new PlayableCard(controller)

  class PlayableCard(controller: T) {
    val source = self
    import reactive._
    val playable: Varying[Boolean] = costs map {
      _.fulfilled(controller)
    } reduceOption{
      _ ~ _ map {
        case a ~ b => a && b
      }
    } getOrElse Var(true)
    def apply(cb: Option[CardResult] => Unit) {
      if (playable()) {
        costs foreach {
          _.applyCost(controller)
        }
        applyImpl(controller)(cb)
      } else {
        throw new RuntimeException(self +" card is applied with insufficient costs!:"+costs)
      }
    }
  }

}
trait Requirement[T] {
  def fulfilled(controller: T): Varying[Boolean]
}

trait Cost[T] extends Requirement[T] {
  def applyCost(controller: T)
}

object Card {
  implicit def FireToCost(fire: Fire) = FireCost(fire)

  implicit def ThunderToCost(thunder: Thunder) = ThunderCost(thunder)

  implicit def waterToCost(water: Water) = WaterCost(water)
}



trait Element {
  val value: Int
}

case class Fire(value: Int)

case class Thunder(value: Int)

case class Water(value: Int)
trait PuzzleCard extends Card[PuzzleGameController]
trait PCost extends Cost[PuzzleGameController]
case class FireCost(fire: Fire) extends PCost {
  def applyCost(controller: PuzzleGameController) {
    controller.game.player.fireMana() -= fire.value
  }

  def fulfilled(controller: PuzzleGameController): Varying[Boolean] = controller.game.player.fireMana map {
    _ >= fire.value
  }
}
case class ThunderCost(thunder: Thunder) extends PCost{
  def applyCost(controller: PuzzleGameController) {
    controller.game.player.thunderMana() -= thunder.value
  }

  def fulfilled(controller: PuzzleGameController): Varying[Boolean] = controller.game.player.thunderMana map {
    _ >= thunder.value
  }
}

case class WaterCost(water: Water) extends PCost{
  def applyCost(controller: PuzzleGameController) {
    controller.game.player.waterMana() -= water.value
  }

  def fulfilled(controller: PuzzleGameController): Varying[Boolean] = controller.game.player.waterMana map {
    _ >= water.value
  }
}


trait CardResult

class Charge extends PuzzleCard {
  def applyImpl(controller: PuzzleGameController)(cb: (Option[CardResult]) => Unit) {
    import controller.game.player._
    fireMana() +=1
    waterMana() += 1
    thunderMana() += 1
    cb(None)
  }
  def costs = Nil
}

class Meteor extends PuzzleCard {
  def applyImpl(controller: PuzzleGameController)(cb: (Option[CardResult]) => Unit) {
    import controller.game.puzzle.{ROW,COLUMN}
    controller.destroy(Random.shuffle(for(x <- 0 until ROW;y <- 0 until COLUMN) yield (x,y)) take (ROW * COLUMN * 0.2).toInt :_*){
      ()=>cb(None)
    }
  }
  def costs  =  FireCost(Fire(1)) :: Nil
}

class AddSwipe extends PuzzleCard {
  def applyImpl(controller: PuzzleGameController)(cb: (Option[CardResult]) => Unit) {
    controller.addSwipeLength(1)
    cb(None)
  }
  def costs  = WaterCost(Water(1)) :: Nil
}
class DrawCard extends PuzzleCard{
  protected def applyImpl(controller: PuzzleGameController)(cb: (Option[CardResult]) => Unit) {
    controller.drawCard()
    controller.drawCard()
    cb(None)
  }
  def costs = WaterCost(Water(1))::FireCost(Fire(1))::ThunderCost(Thunder(1))::Nil
}


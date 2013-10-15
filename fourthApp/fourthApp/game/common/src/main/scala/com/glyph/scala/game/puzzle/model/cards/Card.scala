package com.glyph.scala.game.puzzle.model.cards

import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.util.reactive.{Var, Varying}
import com.glyph.scala.lib.util.reactive
import scala.util.Random

/**
 * @author glyph
 */
trait Card {
  self =>
  protected def applyImpl(controller: PuzzleGameController)(cb: Option[CardResult] => Unit)
  def costs: Seq[Cost]

  def createPlayable(controller: PuzzleGameController): PlayableCard = new PlayableCard(controller)

  class PlayableCard(controller: PuzzleGameController) {
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


object Card {
  implicit def FireToCost(fire: Fire) = FireCost(fire)

  implicit def ThunderToCost(thunder: Thunder) = ThunderCost(thunder)

  implicit def waterToCost(water: Water) = WaterCost(water)
}

trait Requirement {
  def fulfilled(controller: PuzzleGameController): Varying[Boolean]
}

trait Cost extends Requirement {
  def applyCost(controller: PuzzleGameController)
}

trait Element {
  val value: Int
}

case class Fire(value: Int)

case class Thunder(value: Int)

case class Water(value: Int)

case class FireCost(fire: Fire) extends Cost {
  def applyCost(controller: PuzzleGameController) {
    controller.game.player.fireMana() -= fire.value
  }

  def fulfilled(controller: PuzzleGameController): Varying[Boolean] = controller.game.player.fireMana map {
    _ >= fire.value
  }
}

case class ThunderCost(thunder: Thunder) extends Cost {
  def applyCost(controller: PuzzleGameController) {
    controller.game.player.thunderMana() -= thunder.value
  }

  def fulfilled(controller: PuzzleGameController): Varying[Boolean] = controller.game.player.thunderMana map {
    _ >= thunder.value
  }
}

case class WaterCost(water: Water) extends Cost {
  def applyCost(controller: PuzzleGameController) {
    controller.game.player.waterMana() -= water.value
  }

  def fulfilled(controller: PuzzleGameController): Varying[Boolean] = controller.game.player.waterMana map {
    _ >= water.value
  }
}
trait CardResult

class Charge extends Card {
  def applyImpl(controller: PuzzleGameController)(cb: (Option[CardResult]) => Unit) {
    import controller.game.player._
    fireMana() +=1
    waterMana() += 1
    thunderMana() += 1
    cb(None)
  }
  def costs: Seq[Cost] = Nil
}

class Meteor extends Card {
  def applyImpl(controller: PuzzleGameController)(cb: (Option[CardResult]) => Unit) {
    import controller.game.puzzle.{ROW,COLUMN}
    controller.destroy(Random.shuffle(for(x <- 0 until ROW;y <- 0 until COLUMN) yield (x,y)) take (ROW * COLUMN * 0.2).toInt :_*){
      ()=>cb(None)
    }
  }
  def costs: Seq[Cost] =  FireCost(Fire(1)) :: Nil
}

class AddSwipe extends Card {
  def applyImpl(controller: PuzzleGameController)(cb: (Option[CardResult]) => Unit) {
    controller.addSwipeLength(1)
    cb(None)
  }
  def costs: Seq[Cost] = WaterCost(Water(1)) :: Nil
}
class DrawCard extends Card{
  protected def applyImpl(controller: PuzzleGameController)(cb: (Option[CardResult]) => Unit) {
    controller.drawCard()
    controller.drawCard()
    cb(None)
  }
  def costs: Seq[Cost] = WaterCost(Water(1))::FireCost(Fire(1))::ThunderCost(Thunder(1))::Nil
}


package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.game.puzzle.model.cards._
import com.glyph.scala.lib.util.reactive.{Var, Reactor}
import com.glyph.scala.game.puzzle.model.match_puzzle.{Match3, Panel}
import com.glyph.scala.game.puzzle.model.monsters.Monster
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.puzzle.controller.Swiped
import com.glyph.scala.game.puzzle.controller.UseCard

/**
 * Receives events from view, and pass it to the game model
 * this is like an activity of the androids!
 * @author glyph
 */
class PuzzleGameController(val game: Game) extends Reactor {
  //TODO make this swipable

  //sacrifice the spirits to either angel or demons
  /**
   * ３元の精霊を天使か悪魔に捧げることでカードを発動する。
   * カードには中立カード、天使カード、悪魔カードがある
   * 捧げた精霊の比率によってプレイヤーの属性が決定されていく
   * パズルに登場するモンスターは全て天使か悪魔か人間の創造物・・・
   * というような世界観でやってみるか
   * プレイヤは天使か悪魔に近づいていくことになるが、
   * 近づくことによって特典とデメリットを得るようにしたい。
   */


  import game._
  val swipeLength = Var(1,"PuzzleGameController:swipeLength")

  //ゲームのロジックとアニメーションを分けたいんですよね。
  reactVar(player.experience) {
    exp => val floor = (exp / 1000).toInt + 1
      if (floor != player.position()) {
        player.position() = floor
      }
  }
  var destroyAnimation: Option[DestroyAnimation] = None
  var fillAnimation: Option[FillAnimation] = None
  var damageAnimation: Seq[(Monster, Int)] => Animation = {
    seq => {
      f => f()
    }
  }
  var idleInput: (Int)=>(IdleEvent => Unit) => Unit = {
    //dummy
    length=>{
      func => {
        func(UseCard(new Meteor().createPlayable(this)))
      }
    }
  }
  type Animation = (() => Unit) => Unit
  type DestroyAnimation = Seq[(Panel, Int, Int)] => Animation
  type FillAnimation = Seq[(Panel, Int, Int)] => Animation

  def dummy: Animation = block => {
    block()
  }

  def fill: Animation = {
    block => {
      //println("fill")
      val filling = puzzle.createFilling
      puzzle.fill(filling)
      //println("fill=>")
      //println(puzzle.panels().toStr)
      fillAnimation.map {
        _(filling)
      }.getOrElse(dummy) {
        () => scan {
          () => block()
        }
      }
    }
  }

  def addSwipeLength(amount:Int){
    swipeLength() += amount
  }


  def scan: Animation = {
    block => {
      //println("scan")
      val scanned = puzzle.scan() map {
        case (p, x, y) => (x, y)
      }
      if (!scanned.isEmpty) {
        destroy(scanned: _*) {
          () => block()
        }
      } else {
        block()
      }
    }
  }

  def destroy(indices: (Int, Int)*): Animation = {
    block => {
      val snapshot = puzzle.panels()
      //println("destroy=>")
      //println(snapshot.toStr)
      val panels = indices map {
        case (x, y) => (snapshot(x)(y), x, y)
      }
      puzzle.removeIndices(indices: _*)
      destroyAnimation.map {
        _(panels)
      }.getOrElse(dummy) {
        () => fill {
          () => block()
        }
      }
    }
  }

  def addExperience(exp: Float) {
    player.experience() += exp
  }

  def addThunderMana(mana: Float) {
    player.thunderMana <= mana
  }

  def addFireMana(mana: Float) {
    player.fireMana <= mana
  }

  def addWaterMana(mana: Float) {
    player.waterMana <= mana
  }

  def addLife(life: Float) {
    player.hp() += life
  }

  def damage(dmg: Int = 30) {
    game.player.hp() -= dmg
  }

  //val cardSeed = new RJS[() => Card](new GdxFile("js/cardseed.js"))

  // val cardSeed = new RScala[()=>Card](new GdxFile("scala/cardSeed.scala"))
  val cardSeed = () => {
    MathUtils.random(0,3) match{
      case 0 => new Meteor
      case 1 => new Scanner
      case 2 => new AddSwipe
      case 3 => new DrawCard
    }
  }

  def initialize() {
    (1 to 40) foreach {
      _ =>
        deck.addCard(cardSeed())
    }
    (1 to 5) foreach {
      _ => deck.drawCard()
    }
    fill {
      () =>
        idle {
          result =>
        }
    }
  }

  def idle(f: (Any) => Unit) {
    swipeLength() = 1
    val initialMonsters = puzzle.panels().flatten.collect{
      case m:Monster => m
    }
    loop()
    def loop() {
      idleInput(swipeLength()){
        case UseCard(card) => {
          //println("UseCard:"+card)
          card{
            case _ => loop()
          }
          println("hands =>")
          game.deck.hand() foreach println
          println("used card => "+card.source)
          game.deck.discard(card.source)
        }
        case Swiped(record) => {
          record foreach {
            case (a, b, c, d) => puzzle.swap(a, b, c, d)
          }
          deck.hand() foreach discard
          scan {
            () => damagePhase(initialMonsters){
              () =>
                for (_ <- 1 to 5) drawCard()
                idle(f)
            }
          }
        }
      }
    }
  }

  def damagePhase(initMonsters:Seq[Monster])(cb: () => Unit) {
    val currentMonsters =puzzle.panels().flatten.collect {
      case m: Monster => m
    }
    //println("init   =>"+initMonsters)
    //println("current=>"+currentMonsters)
    val deleted = initMonsters diff currentMonsters
    val alive = initMonsters diff deleted
    val monsters = alive
    //println("diff   =>"+monsters)
    (monsters map {
      _.atk
    }).sum match {
      case 0 => cb()
      case dmg => damage(dmg)
        damageAnimation(monsters map {
          m => (m, m.atk)
        }) {
          () => cb()
        }
    }
  }

  def drawCard() {
    deck.drawCard()
  }

  def discard(card: Card) {
    deck.discard(card)
  }
}

trait IdleEvent

case class UseCard(card: Card#PlayableCard) extends IdleEvent

case class Swiped(record: Seq[(Int, Int, Int, Int)]) extends IdleEvent

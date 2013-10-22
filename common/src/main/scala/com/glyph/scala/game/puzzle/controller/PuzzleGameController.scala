package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.model.{PlayableDeck, Game}
import com.glyph.scala.game.puzzle.model.cards._
import com.glyph.scala.lib.util.reactive.{Var, Reactor}
import com.glyph.scala.game.puzzle.model.match_puzzle.{MaybeDestroyed, OnMatch, DestroyEffect}
import com.glyph.scala.game.puzzle.model.monsters.Monster
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.lib.puzzle.Match3
import Match3.Events
import scalaz.Scalaz
import Scalaz._
import com.glyph.scala.lib.puzzle.Match3

/**
 * this is actually a game class...
 * @author glyph
 */
class
PuzzleGameController(val game: Game) extends Reactor with Logging{
  val deck = new PlayableDeck[PuzzleGameController](this)
  type C = Card[PuzzleGameController]
  type PCard = C#PlayableCard
  import PuzzleGameController._
  import Match3._

  /**
   * ３元の精霊を天使か悪魔に捧げることでカードを発動する。
   * カードには中立カード、天使カード、悪魔カードがある
   * 捧げた精霊の比率によってプレイヤーの属性が決定されていく
   * パズルに登場するモンスターは全て天使か悪魔か人間の創造物・・・
   * というような世界観でやってみるか
   * プレイヤは天使か悪魔に近づいていくことになるが、
   * 近づくことによって特典とデメリットを得るようにしたい。
   */
  //TODO 5ターンくらい経過したときにカードを取得出来るように変更
  /**
   * カードの取得方法：画面内のモンスターを、リソースを消費することでカード化する。
   * 敵ごとに必要なリソース数が決まっていることにするか。
   */
  //TODO UI　デザ
  import game._
  class OptIntVar(init:Option[Int] = None,name:String = "PuzzleGameController:undefined") extends Var[Option[Int]](init,name){
    def +=(a:Int){this ()= current map {_ + a}}
    def -=(a:Int){this ()= current map {_ - a}}
  }
  val swipeLength = new OptIntVar
  val actionPoint = new OptIntVar
  swipeLength.update(_.map{_+1})

  val seed = ()=>dungeon.getPanel(player.position())

  //ゲームのロジックとアニメーションを分けたいんですよね。
  reactVar(player.experience) {
    exp => val floor = (exp / 1000).toInt + 1
      if (floor != player.position()) {
        player.position() = floor
      }
  }
  var destroyAnimation: DestroyAnimation = requests=> cb => cb()
  var fillAnimation: FillAnimation = filling => cb => cb()
  var damageAnimation: DamageAnimation = {
    seq => {
      f => f()
    }
  }
  var idleInput: IdleInput = {
    //dummy
    length => {
      func => {
        val card = new Meteor().createPlayable(this)
        if (card.playable()) {
          func(UseCard(card))
        }
      }
    }
  }

  def fill(filling:Events = puzzle.createFilling(seed)): Animation = {
    block => {
      log("fill")
      log(filling)
      puzzle.fill(filling)
      fillAnimation(filling){
        () => scan {
          () => block()
        }
      }
    }
  }

  def addSwipeLength(amount: Int) {
    swipeLength += amount
  }

  def scan: Animation = {
    block => {
      val matchedSets = puzzle.findMatches
      val panelSets = (matchedSets.view map{sets=> sets map{case(p,x,y) => p}}).force

      panelSets.foreach{//do damage calculations and so on...
        sets => sets.collect{case p:OnMatch => p} foreach{_.onMatch(sets)}
      }
      val destroyed = matchedSets.flatten.distinct filter{
        case a@(p:MaybeDestroyed,x,y)=> p.isDestroyed
        case a => true
      } map {
        case (p,x,y) => (x,y)
      }
      if(!destroyed.isEmpty){
        destroy(destroyed:_*){
          ()=> block()
        }
      } else {
        block()
      }
    }
  }

  def destroy(indices: (Int, Int)*): Animation = {
    block => {
      val snapshot = puzzle.panels()
      val panels = indices map {
        case (x, y) => (snapshot(x)(y), x, y)
      }
      puzzle.removeIndices(indices: _*)
      destroyAnimation(panels) {
        () => {
          panels collect { case(p:DestroyEffect,_,_) => p} foreach{_.onDestroy(this)}
          fill() {
            () => block()
          }
        }
      }
    }
  }

  def addExperience(exp: Float) {
    player.experience() += exp
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
    MathUtils.random(0, 3) match {
      case 0 => new Meteor
      case 1 => new Charge
      case 2 => new AddSwipe
      case 3 => new DrawCard
    }
  }

  def initialize() {
    log("initializing game...")
    (1 to 40) foreach {
      _ =>
        deck.addCard(cardSeed().createPlayable(this))
    }
    (1 to 5) foreach {
      _ => deck.drawCard()
    }
    fill(puzzle.createNoMatchFilling(seed)){
      () =>
        idle {
          result =>
        }
    }
    log("initialized")
  }

  def idle(f: (Any) => Unit) {
    val handSwipes = deck.hand map{_.map(_.source).collect {
      case a: AddSwipe => a.move
    }.sum +1}
    swipeLength() = handSwipes().some
    val initialMonsters = puzzle.panels().flatten.collect {
      case m: Monster => m
    }
    loop()
    def loop() {
      idleInput(handSwipes()) {
        case UseCard(card) => {
          //println("UseCard:"+card)
          println("hands =>")
          deck.hand() foreach println
          println("used card => " + card.source)
          deck.discard(card)
          card {
            case _ => loop()
          }
        }
        case Swiped(record) => {
          record foreach {
            case (a, b, c, d) => puzzle.swap(a, b, c, d)
          }
          deck.hand() foreach discard
          scan {
            () => damagePhase(initialMonsters) {
              () =>
                for (_ <- 1 to 5) drawCard()
                idle(f)
            }
          }
        }
      }
    }
  }

  def damagePhase(initMonsters: Seq[Monster])(cb: () => Unit) {
    val currentMonsters = puzzle.panels().flatten.collect {
      case m: Monster => m
    }
    //println("init   =>"+initMonsters)
    //println("current=>"+currentMonsters)
    val deleted = initMonsters diff currentMonsters
    val alive = initMonsters diff deleted
    val monsters = alive
    //println("diff   =>"+monsters)
    (monsters map {
      _.atk()
    }).sum match {
      case 0 => cb()
      case dmg => damage(dmg)
        damageAnimation(monsters map {
          m => (m, m.atk())
        }) {
          () => cb()
        }
    }
  }

  def drawCard() {
    deck.drawCard()
  }

  def discard(card: PCard) {
    deck.discard(card)
  }
}

object PuzzleGameController {
  import Match3._
  type IdleInput = (Int) => (IdleEvent => Unit) => Unit
  type Animation = (() => Unit) => Unit
  type DestroyAnimation = Seq[(Panel, Int, Int)] => Animation
  type FillAnimation = Seq[(Panel, Int, Int)] => Animation
  type DamageAnimation = Seq[(Monster, Int)] => Animation
}

trait IdleEvent

case class UseCard(card: Card[PuzzleGameController]#PlayableCard) extends IdleEvent

case class Swiped(record: Seq[(Int, Int, Int, Int)]) extends IdleEvent

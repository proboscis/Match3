package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.{WidgetGroup, Table}
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.scenes.scene2d.{Touchable, Actor}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.actor.{TouchSource, Layered}
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.libgdx.GdxUtil
import com.glyph.scala.lib.util.reactive
import reactive._

/**
 * @author glyph
 */
class PuzzleGameView(game: Game, controller: PuzzleGameController) extends Table with TouchSource with Reactor {

  import ScalaGame._

  val root = new WidgetGroup with Layered
  val table = new Table
  val cardView = new CardTableView(game.deck)
  val headerView = new HeaderView(game)
  val puzzleView = new PuzzleView(game.puzzle, controller)
  val puzzleGroup = new WidgetGroup with Layered
  val descLayer = new WidgetGroup
  val statusView = new StatusView(game)
  //puzzleView.setSize(VIRTUAL_WIDTH,VIRTUAL_WIDTH)
  puzzleGroup.addActor(puzzleView)
  puzzleGroup.addActor(descLayer)
  table.top()
  table.add().expandX().height(VIRTUAL_WIDTH / 9f * 1.4f).row //dummy for ads
  table.add(headerView).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH / 9f).top.row
  table.add(puzzleGroup).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH).top.row
  table.add(statusView).expandX().height(VIRTUAL_WIDTH / 9f * 0.7f).fill.row
  table.add(cardView).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH / 5f * 1.618f)
  table.row()
  table.debug()
  root.addActor(table)
  table.layout() //somehow this is required
  add(root).fill().expand()
  layout()
  //logic for gameover
  once((game.player.hp ~ statusView.lifeGauge.visualAlpha).toEvents.filter {
    case hp ~ a => hp <= 0 && a <= 0
  }) {
    e => root.addActor(new GameOver)
  }
  //TODO ゲーム全体のステートマシンをどう表現するか
  //TODO モンスタとプレイヤを両方ターンマネージャに追加する必要がある。

  /**
   * GOALを定める
   * 階層月ダンジョンをクリアしたらゲームクリア
   * 敵を倒してカード入手
   * 敵のターンで何かが起きるときはエフェクト、アニメーションが発生する
   * 画面構成を考える
   */
  //TODO actions wrapper を作成
  //TODO TurnManager =>
  /**
   * 　ターン参加者をすべて含むターンマネージャを作成するか
   * Animationを含めた状態遷移の検討が必要
   */

  /**
   * TODO
   * まずはHPを実装、..done
   * フロアを進む方法を実装 ->見た目も
   * 敵のターンを実装->見た目も
   * カード使用によるターン進行を実装 => Cardの効果はcardに記述する
   */
  //もしかすると、多くのビューをスライドインするかも知れない。それを考慮して作るべき。
  private var mState: State = null

  def state_=(s: State) {
    if (mState != null) mState.exit()
    mState = s
    s.enter()
    println("<="+s)
  }

  def state = mState //getter
  this.state = Idle()

  trait State extends Reactor {
    debugReaction(this.toString+"@%x".format(this.hashCode()))
    def enter() {
      println("enter:%s@%x".format(this,this.hashCode()))
    }

    def exit() {
      println("exit:%s@%x".format(this,this.hashCode()))
      State.this.clearReaction()//clear できてない説
      State.this.reactors foreach {println}
    }
  }

  /**
   * event source,
   * display
   */
  type Displayable = (Actor, TouchSource)

  case class Idle() extends State {
    puzzleView.panelTouch.debugReactive("panelTouch")
    cardView.cardPress.debugReactive("cardPress")
    controller.stateChange.debugReactive("stateChange")
    override def enter() {
      super.enter()
      reactEvent(puzzleView.panelTouch) {
        token => {
          state = SlideIn(token, new PanelDescription(token.panel) with TouchSource)
        }
      }
      reactEvent(cardView.cardPress) {
        token => {
          //cardView.removeToken(token)
          state = SlideIn(token, new CardDescription(token.card) with TouchSource)
          //controller.startScanSequence()
        }
      }
      reactEvent(controller.stateChange) {
        case s if s == controller.Animating => state = PuzzleAnimation();println("switch to puzzle animation")
        case _ => println("skip this state")
      }
      println("idle enter end")
    }
  }

  case class PuzzleAnimation() extends State {
    override def enter() {
      super.enter()
      reactEvent(controller.stateChange) {
        case s if s == controller.Idle => state = Idle() //;println("received idle:"+s)
        case s => //println("received:"+s)
      }
    }
  }

  case class SelectPosition() extends State {

  }

  case class ShowDescription(displayable: Displayable) extends State {
    press.debugReactive("PuzzleGameView press")
    override def enter() {
      super.enter()
      val (src, actor) = displayable
      actor.press.debugReactive(actor.getClass.getSimpleName+", description press")
      reactEvent(actor.press) {
        pos =>
          src match {
            case CardToken(card, _, _) => state = SlideOut(displayable, () => {
              card(controller) // use card
              controller.discard(card)
              controller.drawCard()
            })
            case _ => state = SlideOut(displayable)
          }
      }
      reactEvent(cardView.cardPress) {
        token =>
          if (token != src) {
            state = SlideInOut((token, new CardDescription(token.card) with TouchSource), displayable)
          } else {
            state = SlideOut(displayable)
          }
      }
      reactEvent(puzzleView.panelTouch) {
        token => if (token != src) state = SlideInOut((token, new PanelDescription(token.panel) with TouchSource), displayable)
      }
      reactEvent(press) {
        pos => state = SlideOut(displayable)
      }
    }
  }

  case class SlideIn(in: Displayable) extends State {
    override def enter() {
      super.enter()
      val (src, disp) = in
      slideIn(in) {
        state = ShowDescription(in)
      }
      /*
      react(cardView.cardPress) {
        token => {
          if (token == src) {
            disp.clearActions()
            state = SlideOut(in)
          } else {
            state = SlideInOut((token, new CardDescription(token.card) with TouchSource), in)
          }
        }
      }
      react(puzzleView.panelTouch) {
        token =>
          if (token != src) {
            state = SlideInOut((token, new PanelDescription(token.panel) with TouchSource), in)
          } else {
            disp.clearActions()
            state = SlideOut(in)
          }
      }
      */
    }
  }

  case class SlideOut(out: Displayable, f: () => Unit = () => {}) extends State {
    override def enter() {
      super.enter()
      slideOut(out) {
        state = Idle()
        f()
      }
    }
  }

  case class SlideInOut(in: Displayable, out: Displayable) extends State {
    override def enter() {
      super.enter()
      var inDone, outDone = false
      slideIn(in) {
        inDone = true
        if (inDone && outDone) {
          state = ShowDescription(in)
        }
      }
      slideOut(out) {
        outDone = true
        if (inDone && outDone) {
          state = ShowDescription(in)
        }
      }
    }
  }

  def slideIn(disp: Displayable)(f: => Unit) {
    val (_, in) = disp
    val view = descLayer
    descLayer.setTouchable(Touchable.childrenOnly)
    in.setSize(view.getWidth * 0.6f, view.getHeight)
    in.setPosition(view.getWidth, 0)
    import Actions._
    val m = moveTo(view.getWidth - in.getWidth, 0)
    m.setDuration(0.6f)
    m.setInterpolation(Interpolation.exp10Out)
    in.addAction(sequence(m, run(new Runnable {
      def run() {
        f
      }
    })))
    in.toFront()
    GdxUtil.post {
      view.addActor(in)
    }
  }

  def slideOut(disp: Displayable)(f: => Unit) {
    val (_, out) = disp
    import Actions._
    val move = moveTo(descLayer.getWidth, 0)
    move.setDuration(0.3f)
    move.setInterpolation(Interpolation.exp10Out)
    out.addAction(sequence(move, run(new Runnable {
      def run() {
        f
        println("must be removed after this")
      }
    }), Actions.removeActor()))
  }
}

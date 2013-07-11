package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.{WidgetGroup, Table}
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.scenes.scene2d.{Actor, Touchable}
import com.glyph.scala.lib.util.observer.Observing
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.actor.{ObsTouchable, Layered}
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.lib.libgdx.GdxUtil
import com.glyph.scala.lib.util.observer.reactive.Reactor

/**
 * @author glyph
 */
class PuzzleGameView(game: Game, controller: PuzzleGameController) extends Table with ObsTouchable with Reactor {
  //TODO デザインの検討
  //TODO コメントの記述

  import ScalaGame._

  val root = new WidgetGroup with Layered
  val table = new Table
  val cardView = new CardTableView(game.deck)
  val puzzleView = new PuzzleView(game.puzzle, controller)
  val puzzleGroup = new WidgetGroup with Layered
  val descLayer = new WidgetGroup
  val statusView = new StatusView(game)
  //puzzleView.setSize(VIRTUAL_WIDTH,VIRTUAL_WIDTH)
  puzzleGroup.addActor(puzzleView)
  puzzleGroup.addActor(descLayer)
  table.top()
  table.add(puzzleGroup).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH).top
  table.row().top()
  table.add(statusView).expand().fill()
  table.row()
  table.add(cardView).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH / 5f * 1.618f).top()
  table.row()
  table.debug()
  root.addActor(table)
  react(statusView.lifeGauge.visualAlpha() <= 0 && game.player.hp() <= 0) {
    if (_) {
      println("GAMEOVER")
      val gameOver = new GameOver
      import Actions._
      gameOver.setColor(0, 0, 0, 0)
      gameOver.addAction(fadeIn(0.7f, Interpolation.exp10Out))
      root.addActor(gameOver)
    }
  }
  //root.addActor(new GameOver)do this when the game is over
  table.layout() //somehow this is required
  add(root).fill().expand()
  layout()
  //this.addActor(table)

  /**
   * TODO
   * まずはHPを実装、..done
   * フロアを進む方法を実装
   * 敵のターンを実装
   * カード使用によるターン進行を実装 => Cardの効果はcardに記述する
   */
  //もしかすると、多くのビューをスライドインするかも知れない。それを考慮して作るべき。
  private var mState: State = null

  def state_=(s: State) {
    //setter
    if (mState != null) mState.exit()
    mState = s
    s.enter()
  }

  def state = mState //getter
  this.state = Idle()

  trait State extends Observing with Reactor {
    def enter() {
      println("enter:" + this)
    }

    def exit() {
      println("exit:" + this)
      clearObservers()
      clearReaction()
    }
  }

  /**
   * event source,
   * display
   */
  type Displayable = (Actor, ObsTouchable)

  case class Idle() extends State {
    override def enter() {
      super.enter()
      observe(puzzleView.panelTouch) {
        token => {
          state = SlideIn(token, new PanelDescription(token.panel) with ObsTouchable)
        }
      }
      observe(cardView.cardPressed) {
        token => {
          //cardView.removeToken(token)
          state = SlideIn(token, new CardDescription(token.card) with ObsTouchable)
          //controller.startScanSequence()
        }
      }
      react(controller.state) {
        case s if s == controller.Animating => state = PuzzleAnimation()
        case _ =>
      }
    }
  }

  case class PuzzleAnimation() extends State {
    override def enter() {
      super.enter()
      react(controller.state) {
        case s if s == controller.Idle => state = Idle();println("received idle:"+s)
        case s => println("received:"+s)
      }
    }
  }

  case class ShowDescription(displayable: Displayable) extends State {
    override def enter() {
      super.enter()
      val (src, actor) = displayable
      observe(actor.press) {
        pos => src match {
          case CardToken(card, _, _) => state = SlideOut(displayable, () => {
            card(controller) // use card
          })
          case _ => state = SlideOut(displayable)
        }
      }
      observe(cardView.cardPressed) {
        token =>
          if (token != src) {
            state = SlideInOut((token, new CardDescription(token.card) with ObsTouchable), displayable)
          } else {
            state = SlideOut(displayable)
          }
      }
      observe(puzzleView.panelTouch) {
        token => if (token != src) state = SlideInOut((token, new PanelDescription(token.panel) with ObsTouchable), displayable)
      }
      observe(press) {
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
      observe(cardView.cardPressed) {
        token => {
          if (token == src) {
            disp.clearActions()
            state = SlideOut(in)
          } else {
            state = SlideInOut((token, new CardDescription(token.card) with ObsTouchable), in)
          }
        }
      }
      observe(puzzleView.panelTouch) {
        token =>
          if (token != src) {
            state = SlideInOut((token, new PanelDescription(token.panel) with ObsTouchable), in)
          } else {
            disp.clearActions()
            state = SlideOut(in)
          }
      }
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
      }
    }), Actions.removeActor()))
  }
}

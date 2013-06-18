package com.glyph.scala.game.puzzle.controller

import com.glyph.scala.game.puzzle.view.{PanelToken, PuzzleView}
import com.glyph.scala.game.puzzle.model.Puzzle
import com.glyph.scala.lib.util.updatable.Updatables
import com.glyph.scala.lib.util.collection.list.DoubleLinkedQueue
import com.badlogic.gdx.scenes.scene2d.actions.{MoveToAction, Actions}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.observer.{Observable, Observing}
import com.glyph.scala.lib.util.Disposable
import com.glyph.scala.game.puzzle.model.panels.Panel
import com.glyph.scala.lib.libgdx.actor.action.Waiter
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.util.updatable.task._

/**
 * @author glyph
 */
class PuzzleViewController(view: PuzzleView, puzzle: Puzzle) extends Updatables with Observing with Disposable {

  import Actions._
  import com.glyph.scala.lib.util.Implicit._

  val scanning = new Observable[Boolean]
  val sequencer = new SequentialProcessor {}
  this add sequencer
  //val pool = new ParticlePool[SpriteParticle](classOf[SpriteParticle], 1000)
  val tokens = Array(puzzle.panels map {
    column => new DoubleLinkedQueue[PanelToken]
  }: _*)

  //TODO スキャン中はカードを使えない様にする。
  //TODO モデルをロックしたい。
  /**
   *
   * スキャンされた時に呼ばれる。
   * アニメーション後にもう一度スキャンする。
   * ロジックがここに書かれるのは非常に良くないとは思うが・・・今のうち
   */
  puzzle.onScan update {
    case (removes, adds) => {
      scanning(true)
      sequencer add Sequence(
        Parallel(
          removes flatMap {
            case (p, x, y) => tokens(x) filter {
              token => token.panel eq p
            } map {
              token =>
              //パネルのアニメ終了まで待つ
                Wait(wait => {
                  //TODOなぜかrunがすぐに実行される問題=>implicit conversionのせい
                  token.explode{
                    token.remove
                    tokens(x).remove(token)
                    wait.wake()
                  }
                })
            }
          }: _*),
        Do {
          adds foreach {
            case (p, x, y) => createPanelToken(p, x, y)
          }
          if (!removes.isEmpty && !adds.isEmpty) {
            setupTokenPosition {
              puzzle.scan()
            }
          }else{
            scanning(false)
          }
        })
    }
  }
  puzzle.onPanelAdded update {
    case (p, x, y) => {
      createPanelToken(p, x, y)
      setupTokenPosition()
    }
  }

  def createPanelToken(panel: Panel, x: Int, y: Int) {
    val p = new PanelToken(panel)
    //初期サイズの設定
    p.setSize(view.panelW, view.panelH)
    //拡大原点の設定
    p.setOrigin(p.getWidth / 2, p.getHeight / 2)
    //列の上部に初期配置する。
    p.setPosition(calcPanelX(x), calcPanelY(puzzle.ROW - puzzle.panels(x).size + puzzle.ROW))
    tokens(x).enqueue(p)
    Gdx.app.postRunnable {
      () =>
      //追加処理はrenderスレッド前で
        view.puzzleGroup.addActor(p)
    }
  }

  private def calcPanelX(x: Int): Float = view.divX * (x + 0.5f) - view.panelW / 2f

  private def calcPanelY(y: Int): Float = view.divY * (y + 0.5f) - view.panelH / 2f

  /**
   * 全てのトークンのアニメーション先を計算、設定、開始する。
   */
  def setupTokenPosition(f: => Unit = {}) {
    Gdx.app.postRunnable {
      () =>
        val waiter = Waiter(f)
        var x = 0
        var y = 0
        while (x < puzzle.COLUMN) {
          val list = tokens(x)
          y = 0
          list.foreach {
            token => {
              val move = action(classOf[MoveToAction])
              move.setPosition(calcPanelX(x), calcPanelY(y))
              move.setDuration(0.5f)
              move.setInterpolation(Interpolation.exp10Out)
              token.clearActions()
              token.addAction(sequence(move, waiter.await()))
              y += 1
            }
          }
          x += 1
        }
    }
  }

  def dispose() {
    clearObserver()
  }
}

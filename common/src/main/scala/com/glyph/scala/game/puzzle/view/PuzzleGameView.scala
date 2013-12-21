package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.{Image, WidgetGroup, Table}
import com.glyph.scala.game.puzzle.model.{PlayableDeck, Game}
import com.glyph.scala.lib.libgdx.actor.{ReactiveSize, TouchSource}
import com.glyph.scala.game.puzzle.controller.{IdleEvent, Swiped, UseCard, PuzzleGameController}
import com.glyph.scala.lib.util.{Logging, reactive}
import reactive._
import com.glyph.scala.lib.libgdx.actor.ui.{Reaction, RLabel, Gauge, SlideView}
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.TextureUtil
import com.badlogic.gdx.scenes.scene2d.{Action, Touchable}
import com.glyph.scala.lib.util.rhino.Rhino
import com.glyph.scala.game.puzzle.view.match3.{ColorTheme, Match3View}
import com.glyph.scala.lib.libgdx.actor.action.{MyActions, Oscillator}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.glyph.scala.game.puzzle.model.cards.Card
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.widgets.Layered
import scalaz._
import Scalaz._
/**
 * ここでcontrollerを渡したことが間違いだった!
 * @author glyph
 */
class PuzzleGameView(assets: AssetManager, val game: Game, deck: PlayableDeck[PuzzleGameController], size: (Int, Int)) extends Table with TouchSource with Reactor with Logging {


  import PuzzleGameController._

  /**
   * 面白さを煮詰めたミニゲームの予定ではなかったのか！？
   * ということで、そうする。
   *
   * カードゲームを一人で行うには、ルールが必要となる
   * カードだけで操作を行う点を考慮しなければ。RPG的な世界観は別にどうでもいい。
   * インフレエンドレスか？１ゲーム制か？
   * 意外と、カードでコンボし続ける状況は悪くない。
   * カードでコンボした後、さらに考えて行動しなければならない点が良くない！
   * となると、マッチ３であっても、コンボのみで完結していれば良いことになる。(!!!)
   *
   */

  val ET = ColorTheme
  //TODOスワイプでカード使用
  //TODO 動いている感をparticleなどで表現
  //TODO スワイプ量の表示
  //TODO スワイプカードの数によってスワイプ数を決める
  //TODO パネルはエレメントではなく、敵、ライフ、アクション、金、盾とする。
  //TODO シンプルなパズルを基本とする。
  setSize(size._1, size._2)
  val root = new WidgetGroup with Layered
  val table = new Table
  val cardView = new CardTableView(assets, deck)
  val headerView = new HeaderView(assets, game) with ReactiveSize
  val puzzleView = new Match3View(assets, game.puzzle)
  val puzzleGroup = new WidgetGroup with Layered
  val slideView = new SlideView(RVJSON(GdxFile("js/view/slideView.js")))
  val config = RVJSON(GdxFile("js/view/manaGauge.js"))

  val swipeLength = new Table() {
    add(new RLabel(skin(assets), puzzleView.visualSwipeLength.map {
      case Some(x) => "" + x
      case None => "*"
    }) with Reaction[String] {
      def reaction: Action = (for {
        conf <- config()
        height <- conf.height.as[Float]
        duration <- conf.duration.as[Float]
      } yield {
        MyActions.jump(height, duration)
      }) getOrElse MyActions.NullAction
    }).fill.expand.center()
  }
  val lifeGauge = new Gauge(assets, game.player.hp ~ game.player.maxHp map {
    case hp ~ max => hp / max
  }, true) with ReactiveSize
  val fireGauge = new ManaGauge(assets, game.player.fireMana, ET.fire) with ReactiveSize
  val waterGauge = new ManaGauge(assets, game.player.waterMana, ET.water) with ReactiveSize
  val thunderGauge = new ManaGauge(assets, game.player.thunderMana, ET.thunder) with ReactiveSize

  val colors = RVJSON(GdxFile("js/view/panelView.js"))

  implicit def strToColor(hex: String): Color = Color.valueOf(hex)

  reactVar(colors) {
    scheme => for {
      schm <- scheme
      fire <- schm.fire.as[String]
      water <- schm.water.as[String]
      thunder <- schm.thunder.as[String]
      life <- schm.life.as[String]
    } {
      fireGauge.setColor(fire)
      waterGauge.setColor(water)
      thunderGauge.setColor(thunder)
      lifeGauge.setColor(life)
    }
  }

  log("PuzzleGameView:w,h=>" + getWidth + "," + getHeight)

  val setup = Rhino(GdxFile("js/view/gameView.js").map {
    _.toOption | ""
  }, Map(
    "self" -> this,
    "root" -> root,
    "table" -> table,
    "cardView" -> cardView,
    "headerView" -> headerView,
    "puzzleView" -> puzzleView,
    "puzzleGroup" -> puzzleGroup,
    "swipeLength" -> swipeLength,
    "slideView" -> slideView,
    "lifeGauge" -> lifeGauge,
    "fireGauge" -> fireGauge,
    "waterGauge" -> waterGauge,
    "thunderGauge" -> thunderGauge,
    "VIRTUAL_WIDTH" -> getWidth,
    "VIRTUAL_HEIGHT" -> getHeight)
  ).asFunction
  reactVar(setup) {
    _.foreach {
      _()
    }
  }
  //logic for gameover... this should not be here.
  once((game.player.hp ~ lifeGauge.visualAlpha).toEvents.filter {
    case hp ~ a => hp <= 0 && a <= 0
  }) {
    e => root.addActor(new GameOver(assets))
  }

  //controller.fillAnimation = Some(puzzleView.fillAnimation)
  //controller.destroyAnimation = Some(puzzleView.destroyAnimation)

  /**
   * make sure to clear out all the side effects when callback
   */
  val idleInput: (Int) => (IdleEvent => Unit) => Unit = {
    swipeLength => {
      callback => {
        puzzleView.setTouchable(Touchable.enabled)
        reactEvent(cardView.cardPress) {
          token => val view = new PlayableCardDescription(assets, token.card) with TouchSource
            if (slideView.shown.exists {
              _ == view
            }) {
              out()
            } else {
              in(view)
            }
        }
        once(slideView.shownPress) {
          case PlayableCardDescription(assets, card) => {
            stop()
            callback(UseCard(card))
          }
          case _ => throw new RuntimeException("Cant happen here.")
        }
        puzzleView.startSwipeCheck(swipeLength) {
          record => {
            stop()
            callback(Swiped(record))
          }
        }
        cardView.startSwipeCheck {
          token => {
            stop()
            callback(UseCard(token.card))
            log("swiped token:" + token)
          }
        }
        reactEvent(puzzleView.press) {
          p => out()
        }
        def stop() {
          puzzleView.setTouchable(Touchable.disabled)
          stopReact(slideView.shownPress)
          stopReact(cardView.cardPress)
          stopReact(puzzleView.press)
          puzzleView.stopSwipeCheck()
          cardView.stopSwipeCheck()
          out()
        }
      }
    }
  }

  def in(view: TouchSource) {
    slideView.slideIn(view, outDone = () => {
      view.dispose()
    })
  }

  def out() {
    slideView.slideOut()
  }

  val dmgEffect = RVJSON(GdxFile("js/effect/dmgEffect.js"))
  val damageAnimation: DamageAnimation = {
    monsters => {
      callback => {
        for {
          de <- dmgEffect()
          a <- de.alpha.as[Float]
          col <- de.color.as[Color]
          inD <- de.in.duration.as[Float]
          outD <- de.out.duration.as[Float]
          inI <- de.in.interpolation.as[Interpolation]
          outI <- de.out.interpolation.as[Interpolation]
        } {
          val oscillator = new Oscillator(0, 5, 10)
          oscillator.setDuration(0.4f)
          table.addAction(oscillator)
          // root.addActor()
          import Actions._
          val img = new Image()
          img.setDrawable(new TextureRegionDrawable(new TextureRegion(TextureUtil.dummy(assets))))
          root.addActor(img)
          img.setColor(col)
          val fadeInOut = sequence(alpha(a, inD, inI), fadeOut(outD, outI), run(new Runnable() {
            def run() {
              callback()
            }
          }), Actions.removeActor())
          img.addAction(fadeInOut)
        }
      }
    }
  }
}

case class PlayableCardDescription(assets: AssetManager, card: Card[PuzzleGameController]#PlayableCard) extends BaseCardDescription(assets, card.source)

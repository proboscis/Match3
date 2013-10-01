package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.{Image, WidgetGroup, Table}
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.libgdx.actor.{ReactiveSize, TouchSource, Layered}
import com.glyph.scala.game.puzzle.controller.{Swiped, UseCard, PuzzleGameController}
import com.glyph.scala.lib.util.reactive
import reactive._
import com.glyph.scala.lib.libgdx.actor.ui.{Gauge, SlideView}
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.math.{MathUtils, Rectangle, Vector2, Interpolation}
import com.glyph.scala.lib.libgdx.{GdxUtil, TextureUtil}
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.glyph.scala.lib.util.rhino.Rhino
import com.glyph.scala.game.puzzle.view.match3.Match3View
import com.glyph.scala.lib.libgdx.actor.action.Oscillator
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.glyph.java.asset.AM
import com.glyph.scala.game.puzzle.model.Element.{Fire, Water, Thunder}
import com.glyph.scala.game.puzzle.model.match_puzzle.Life
import com.glyph.scala.game.puzzle.model.monsters.Monster
import com.glyph.scala.lib.libgdx.particle.Emission
import com.glyph.java.particle.{SpriteParticle, ParticlePool}

/**
 * ここでcontrollerを渡したことが間違いだった!
 * @author glyph
 */
class PuzzleGameView(val game: Game, val controller: PuzzleGameController) extends Table with TouchSource with Reactor {
  //TODO 動いている感をparticleなどで表現
  import ScalaGame._
  val root = new WidgetGroup with Layered
  val table = new Table
  val cardView = new CardTableView(game.deck,controller)
  val headerView = new HeaderView(game) with ReactiveSize
  val puzzleView = new Match3View(game.puzzle)
  val puzzleGroup = new WidgetGroup with Layered
  val slideView = new SlideView(RJSON(GdxFile("js/view/slideView.js").getString))
  val lifeGauge = new Gauge(game.player.hp ~ game.player.maxHp map {
    case hp ~ max => hp / max
  }, true) with ReactiveSize
  val fireGauge = new ManaGauge(game.player.fireMana) with ReactiveSize
  val waterGauge = new ManaGauge(game.player.waterMana) with ReactiveSize
  val thunderGauge = new ManaGauge(game.player.thunderMana) with ReactiveSize

  val colors = RJSON(GdxFile("js/view/panelView.js").getString)

  implicit def strToColor(hex: String): Color = Color.valueOf(hex)

  reactVar(colors) {
    scheme => for {
      fire <- scheme.fire.as[String]
      water <- scheme.water.as[String]
      thunder <- scheme.thunder.as[String]
      life <- scheme.life.as[String]
    } {
      fireGauge.setColor(fire)
      waterGauge.setColor(water)
      thunderGauge.setColor(thunder)
      lifeGauge.setColor(life)
    }
  }
  val setup = Rhino(GdxFile("js/view/gameView.js").getString, Map(
    "self" -> this,
    "root" -> root,
    "table" -> table,
    "cardView" -> cardView,
    "headerView" -> headerView,
    "puzzleView" -> puzzleView,
    "puzzleGroup" -> puzzleGroup,
    "slideView" -> slideView,
    "lifeGauge" -> lifeGauge,
    "fireGauge" -> fireGauge,
    "waterGauge" -> waterGauge,
    "thunderGauge" -> thunderGauge,
    "VIRTUAL_WIDTH" -> VIRTUAL_WIDTH,
    "VIRTUAL_HEIGHT" -> VIRTUAL_HEIGHT)
  ).asFunction
  reactSome(setup)(_())
  //logic for gameover... this should not be here.
  once((game.player.hp ~ lifeGauge.visualAlpha).toEvents.filter {
    case hp ~ a => hp <= 0 && a <= 0
  }) {
    e => root.addActor(new GameOver)
  }

  controller.fillAnimation = Some(puzzleView.fillAnimation)
  controller.destroyAnimation = Some(puzzleView.destroyAnimation)

  /**
   * make sure to clear out all the side effects when callback
   */
  controller.idleInput = {
    swipeLength => {
      callback => {
        puzzleView.setTouchable(Touchable.enabled)
        reactEvent(cardView.cardPress) {
          token => val view = new PlayableCardDescription(token.card) with TouchSource
            in(view)
        }
        once(slideView.shownPress) {
          case PlayableCardDescription(card)=> {
            //println("pressed card description")
            slideView.slideOut()
            puzzleView.setTouchable(Touchable.disabled)
            stopReact(slideView.shownPress)
            stopReact(cardView.cardPress)
            puzzleView.stopSwipeCheck()
            callback(UseCard(card))
          }
          case _ => throw new RuntimeException("Cant happen here.")
        }
        puzzleView.startSwipeCheck(swipeLength) {
          record => {
            stopReact(slideView.shownPress)
            stopReact(cardView.cardPress)
            puzzleView.stopSwipeCheck()
            callback(Swiped(record))
          }
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
  val dmgEffect = RJSON(GdxFile("js/effect/dmgEffect.js").getString)
  controller.damageAnimation = {
    monsters => {
      callback => {
        for {
          a <- dmgEffect().alpha.as[Float]
          col <- dmgEffect().color.as[Color]
          inD <- dmgEffect().in.duration.as[Float]
          outD <- dmgEffect().out.duration.as[Float]
          inI <- dmgEffect().in.interpolation.as[Interpolation]
          outI <- dmgEffect().out.interpolation.as[Interpolation]
        } {
          val oscillator = new Oscillator(0, 5, 10)
          oscillator.setDuration(0.4f)
          table.addAction(oscillator)
          // root.addActor()
          import Actions._
          val img = new Image()
          img.setDrawable(new TextureRegionDrawable(new TextureRegion(TextureUtil.dummy)))
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

  val pool = new ParticlePool(classOf[SpriteParticle], 1000)
  val expEffect = RJSON(GdxFile("js/effect/expParticle.js").getString)
  lazy val region: TextureRegion = new TextureRegion(AM.instance().get("data/particle.png", classOf[Texture]))
  reactEvent(puzzleView.tokenExplosion) {
    token =>
      for {
        speed <- expEffect().speed.as[Float]
        alpha <- expEffect().alpha.as[Float]
        power <- expEffect().power.as[Float]
        duration <- expEffect().duration.as[Float]
        minN <- expEffect().minNumber.as[Int]
        maxN <- expEffect().maxNumber.as[Int]
        minSize <- expEffect().minSize.as[Int]
        maxSize <- expEffect().maxSize.as[Int]
      } {
        val (view, reaction) = (token.panel match {
          case t: Thunder => (thunderGauge, () => controller.addThunderMana(1))
          case t: Water => (waterGauge, () => controller.addWaterMana(1))
          case t: Fire => (fireGauge, () => controller.addFireMana(1))
          case l: Life => (lifeGauge, () => controller.addLife(1))
          case m: Monster => (headerView, () => controller.addExperience(1))
          case _ => (headerView, null)
        }) match {
          case (a, b) => (a, Option(b))
        }
        import MathUtils._
        val emission = new Emission(
          view.rRect map {
            rect => val pos = headerView.getParent.localToStageCoordinates(new Vector2(rect.x, rect.y))
              new Rectangle(pos.x, pos.y, rect.width, rect.height)
          }, pool, duration, alpha, power)
        for (_ <- 1 until random(minN, maxN)) {
          emission += pool.obtain()
        }
        emission.particles foreach {
          p => p.init(region)
            p.setColor(token.getColor)
            //TODO 座標系と色の修正
            val size = random(minSize, maxSize)
            p.setSize(size, size)
            val pos = new Vector2(token.getX, token.getY)
            puzzleView.localToStageCoordinates(pos)
            p.setPosition(pos.x + random(token.getWidth), pos.y + random(token.getHeight))
            val rand = new Vector2(0, 1)
            rand.rotate(random(360))
            rand.scl(random(speed))
            p.getVelocity.add(rand)
        }
        emission.setTouchable(Touchable.disabled)
        GdxUtil.post {
          root.addActor(emission)
        }
        reactEvent(emission.hitEvent) {
          p => reaction.foreach {
            _()
          }
        }
        once(emission.finishEvent) {
          stopReact(emission.hitEvent)
        }
      }
  }
}

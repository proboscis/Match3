package com.glyph._scala.game.action_puzzle.view

import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.ui.{Table, Skin}
import com.glyph._scala.game.action_puzzle._
import com.glyph._scala.lib.util.updatable.reactive.Eased
import com.badlogic.gdx.math.Interpolation
import com.glyph._scala.lib.libgdx.actor.{SpriteActor, Updating}
import com.glyph._scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion, Sprite}
import com.glyph._scala.lib.util.reactive.Reactor
import com.glyph._scala.lib.util.{Threading, Logging}
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.game.action_puzzle.screen.{Trailed, Scoring}
import com.glyph._scala.lib.util.json.RVJSON
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import com.glyph._scala.game.Glyphs
import Glyphs._
import scalaz.Scalaz
import Scalaz._
import com.glyph._scala.lib.libgdx.game.LimitDelta
import com.glyph._scala.lib.util.pool.Pooling
import scala.concurrent.{Await, Future, ExecutionContext}
import com.glyph._scala.lib.libgdx.font.FontUtil
import scala.concurrent.duration.Duration
import com.glyph._scala.lib.injection.GLExecutionContext

/**
 * @author glyph
 */
class ActionPuzzleTable(roundTex: Texture, particleTex: Texture, dummyTex: Texture, skin: Skin) extends Table with Reactor with Logging {
  //TODO implement the basic system.
  //TODO GameOver
  //TODO Title screen
  //TODO Loading screen
  //TODO design the gauge
  //TODO what you can do to inject the dependency is to split the procedure into functions,
  //TODO and making a class as trait as much as possible
  //TODO so, do not declare val as much as possible. reusable objects do not have public values
  //TODO do the initialization on another thread

  /**
   * which operation do you think is the most heavy ???
   */
  val game = new ComboPuzzle

  import game._

  val easedScore = Eased(score map (_.toFloat), Interpolation.exp10Out.apply, _ / 10f)

  val particleRenderer = Await.result(ParticleRenderer.futureRenderer[MyTrail](particleTex), Duration.Inf)

  val view = new APView[Int, SpriteActor](game.puzzle)(new Pooling[SpriteActor] {
    override def newInstance: SpriteActor = new SpriteActor()

    override def reset(tgt: SpriteActor): Unit = {
      tgt.reset()
      tgt.sprite.setTexture(roundTex)
      tgt.sprite.asInstanceOf[TextureRegion].setRegion(0, 0, roundTex.getWidth, roundTex.getHeight)
    }
  }, classOf[SpriteActor])
    with Scoring[Int, SpriteActor]
    with Trailed[Int, SpriteActor]
    with Updating {
    def score: Int = game.score()

    override def trailRenderer: ParticleRenderer[MyTrail] = particleRenderer

    override val font: BitmapFont = skin.get("font/corbert.ttf", classOf[BitmapFont])
  }
  view.add(easedScore)
  val scoreLabel = new RLabel(skin, easedScore.map("%.0f".format(_)))
  scoreLabel.setColor(Color.DARK_GRAY)

  import Actions._
  import Interpolation._

  reactVar(score) {
    val ic = scoreLabel.getColor.cpy()
    var prevAction: Action = null
    s => {
      if (prevAction != null) {
        scoreLabel.removeAction(prevAction)
      }
      prevAction = sequence(color(Color.WHITE, 0.5f, exp10Out), color(ic, 1f, exp10In))
      scoreLabel.addAction(prevAction)
    }
  }
  val comboLabel = new RLabel(skin, combo map (_.toString))
  val inner = new Table()
  inner.debug
  inner.add(scoreLabel).expand
  inner.add(comboLabel).expand
  this.add(inner).fill.expand.row
  val apViewCell = this.add(view).fill().expand()
  apViewCell.left.row

  val gaugeCell = this.add(new Table {
    val back = SpriteActor(new Sprite(dummyTex))
    add(back).fill.expand
    time map (_ / 60f * getWidth) += back.setWidth
  })
  gaugeCell.fill().expand()
  game.onPanelAdd = view.panelAdd
  game.onPanelRemove = seq => {
    view.panelRemove(seq)
  }


  reactSome(view.swipeChecker) {
    case checker => {
      view.swipeStopper()
      checker(puzzle.pooledSwipe)
      //view.startSwipeCheck(puzzle.pooledSwipe)
    }
  }
  this.debug()
  /*
  init after the layout is setup
   */
  puzzle.initialize()

  override def layout(): Unit = {
    apViewCell.size(getWidth, getWidth)
    gaugeCell.size(getWidth, getWidth / 10)
    super.layout()
  }

  override def act(delta: Float): Unit = {
    game.update(delta)
    super.act(delta)
  }
}

object ActionPuzzleTable extends Logging with Threading {
  val requiredAssets: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture] -> Seq("data/dummy.png", "data/particle.png", "data/sword.png", "data/round_rect.png"),
    classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")
  )
  val toScreen = (table: ActionPuzzleTable) => new ConfiguredScreen with LimitDelta {
    backgroundColor = ColorTheme.varyingColorMap()("asbestos")

    override def configSrc: RVJSON = RVJSON(GdxFile("json/actionPuzzleConfig.js"))

    reactVar(config.background.as[String] ~ ColorTheme.varyingColorMap) {
      case str ~ map => backgroundColor = map.lift(str | "") | Color.WHITE
    }
    root.add(table).size(STAGE_WIDTH, STAGE_HEIGHT)
    root.debug()
  }


  def futurePuzzle(roundTex: Texture, particleTex: Texture, dummyTex: Texture, skin: Skin): Future[Table] = {
    val global = ExecutionContext.Implicits.global
    val gl = GLExecutionContext
    val game = new ComboPuzzle
    import game._
    val easedScore = Eased(score map (_.toFloat), Interpolation.exp10Out.apply, _ / 10f)
    val futureRenderer = ParticleRenderer.futureRenderer[MyTrail](particleTex)
    val futureCorbert = Future(FontUtil.internalFont("font/corbert.ttf", 50))(gl)
    val view = futureCorbert.flatMap {
      corbert =>
        futureRenderer.map {
          renderer =>
            log("creating APView")
            new APView[Int, SpriteActor](game.puzzle)(new Pooling[SpriteActor] {
              override def newInstance: SpriteActor = new SpriteActor()

              override def reset(tgt: SpriteActor): Unit = {
                tgt.reset()
                tgt.sprite.setTexture(roundTex)
                tgt.sprite.asInstanceOf[TextureRegion].setRegion(0, 0, roundTex.getWidth, roundTex.getHeight)
              }
            }, classOf[SpriteActor])
              with Scoring[Int, SpriteActor]
              with Trailed[Int, SpriteActor]
              with Updating {
              def score: Int = game.score()

              override def font: BitmapFont = corbert

              override def trailRenderer: ParticleRenderer[MyTrail] = renderer
            }
        }(gl)
    }(gl)
    view.map(apView => new Table with Reactor with Logging with Threading {
      log("initializing a table")
      apView.add(easedScore)
      val scoreLabel = new RLabel(skin, easedScore.map("%.0f".format(_)))
      scoreLabel.setColor(Color.DARK_GRAY)

      import Actions._
      import Interpolation._

      reactVar(score) {
        val ic = scoreLabel.getColor.cpy()
        var prevAction: Action = null
        s => {
          if (prevAction != null) {
            scoreLabel.removeAction(prevAction)
          }
          prevAction = sequence(color(Color.WHITE, 0.5f, exp10Out), color(ic, 1f, exp10In))
          scoreLabel.addAction(prevAction)
        }
      }
      val comboLabel = new RLabel(skin, combo map (_.toString))
      val inner = new Table()
      inner.debug
      inner.add(scoreLabel).expand
      inner.add(comboLabel).expand
      this.add(inner).fill.expand.row
      val apViewCell = this.add(apView).fill().expand()
      apViewCell.left.row
      val gaugeCell = this.add(new Table {
        val back = SpriteActor(new Sprite(dummyTex))
        add(back).fill.expand
        time map (_ / 60f * getWidth) += back.setWidth
      })
      gaugeCell.fill().expand()
      game.onPanelAdd = apView.panelAdd
      game.onPanelRemove = seq => {
        apView.panelRemove(seq)
      }
      reactSome(apView.swipeChecker) {
        case checker => {
          apView.swipeStopper()
          checker(puzzle.pooledSwipe)
          //view.startSwipeCheck(puzzle.pooledSwipe)
        }
      }
      this.debug()
      /*
      init after the layout is setup
       */
      puzzle.initialize()

      override def layout(): Unit = {
        apViewCell.size(getWidth, getWidth)
        gaugeCell.size(getWidth, getWidth / 10)
        super.layout()
      }

      override def act(delta: Float): Unit = {
        game.update(delta)
        super.act(delta)
      }
    })(global)
  }
}
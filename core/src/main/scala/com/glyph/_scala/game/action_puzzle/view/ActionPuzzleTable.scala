package com.glyph._scala.game.action_puzzle.view

import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Table, Skin}
import com.glyph._scala.game.action_puzzle._
import com.glyph._scala.lib.util.updatable.reactive.Eased
import com.badlogic.gdx.math.Interpolation
import com.glyph._scala.lib.libgdx.actor.{AnimatedTable, SpriteActor, Updating}
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
import com.glyph._scala.lib.libgdx.font.FontUtil
import com.glyph._scala.lib.libgdx.actor.action.ActionOps
import com.glyph._scala.lib.libgdx.gl.{UVTrail, BaseStripBatch, ShaderHandler}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.libgdx.actor.widgets.Center

class ActionPuzzleTable(game: ComboPuzzle)(roundTex: Texture, particleTex: Texture, dummyTex: Texture, skin: Skin)
  extends Table
  with Reactor
  with Logging
  with Threading {
  var gameOverCallback: () => Unit = () => {}

  import game._

  val renderer = new ParticleRenderer[MyTrail](particleTex)(ShaderHandler("shader/rotate2.vert", "shader/default.frag"), new BaseStripBatch(1000 * 10 * 2, UVTrail.ATTRIBUTES))
  // you must regenerate this after context loss...
  val corbert = FontUtil.internalFont("font/corbert.ttf", 50)

  val apView = new APView[Int, SpriteActor](game.puzzle)(new Pooling[SpriteActor] {
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

  val easedScore = Eased(score map (_.toFloat), Interpolation.exp10Out.apply, _ / 10f)
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
  game.onGameOver = () => {
    val label = Center(new Label("Time Up", skin)) <| (_.setBackground(skin.getDrawable("emerald")))
    import Actions._
    import Interpolation._
    label.setSize(getWidth,getHeight/5)

    val height = getHeight / 2 - label.getHeight / 2
    label.setPosition(getWidth, height)

    apView.removeAllToken()
    label.addAction(sequence(
      parallel(
        moveTo(getWidth / 2 - label.getWidth / 2, height, 0.5f, exp10Out),
        fadeIn(0.5f,exp10Out)
      ),
      fadeOut(1,exp10In),
      ActionOps.run(() => {
        gameOverCallback()
      })
    ))
    addActor(label)
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

  override def act(delta: Float): Unit = {
    game.update(delta)
    super.act(delta)
  }

  override def layout(): Unit = {
    apViewCell.size(getWidth, getWidth)
    gaugeCell.size(getWidth, getWidth / 10)
    super.layout()
  }
}

object ActionPuzzleTable extends Logging with Threading {
  val requiredAssets: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture] -> Seq("data/dummy.png", "data/particle.png", "data/sword.png", "data/round_rect.png"),
    classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")
  )
  val toScreen = (table: Table) => new ConfiguredScreen with LimitDelta {
    backgroundColor = ColorTheme.varyingColorMap()("asbestos")

    override def configSrc: RVJSON = RVJSON(GdxFile("json/actionPuzzleConfig.js"))

    reactVar(config.background.as[String] ~ ColorTheme.varyingColorMap) {
      case str ~ map => backgroundColor = map.lift(str | "") | Color.WHITE
    }
    root.add(table).size(STAGE_WIDTH, STAGE_HEIGHT)
    root.debug()
  }

  def animated(game: ComboPuzzle)(roundTex: Texture, particleTex: Texture, dummyTex: Texture, skin: Skin): AnimatedConstructor = {
    val view = new ActionPuzzleTable(game)(roundTex, particleTex, dummyTex, skin)
    info => callbacks =>
      val t = new AnimatedTable()
      t.add(view).fill.expand
      view.gameOverCallback = () => {
        callbacks("game_over")(Map("score"->game.score()))
      }
      t
  }
}
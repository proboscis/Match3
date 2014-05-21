package com.glyph._scala.game.action_puzzle.view

import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Table, Skin}
import com.glyph._scala.game.action_puzzle._
import com.glyph._scala.lib.util.updatable.reactive.Eased
import com.badlogic.gdx.math._
import com.glyph._scala.lib.libgdx.actor.{Scissor, AnimatedTable, SpriteActor, Updating}
import com.glyph._scala.lib.libgdx.actor.ui.{Log, LogView, RLabel}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.{Actor, Touchable, Action}
import com.badlogic.gdx.graphics.g2d.{Batch, BitmapFont, TextureRegion, Sprite}
import com.glyph._scala.lib.util.reactive.Reactor
import com.glyph._scala.lib.util.{Threading, Logging}
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.game.action_puzzle.screen.Scoring
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
import com.glyph._scala.lib.libgdx.gl.ShaderHandler
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.libgdx.actor.widgets.{Layered, Center}
import com.glyph._scala.lib.util.updatable.task._
import com.badlogic.gdx.utils.Scaling
import com.glyph._scala.lib.ecs.{Entity, Template, Scene}
import com.glyph._scala.lib.ecs.system.{SpriteRenderer, TrailRenderer}
import com.glyph._scala.lib.ecs.component.{Tint, Velocities, Transform}
import com.glyph._scala.lib.ecs.script._
import com.glyph._scala.game.action_puzzle.view.animated.TableValueOps._
import com.glyph._scala.lib.ecs.script.task.{EntityTaskProcessor}
import com.glyph._scala.game.action_puzzle.ecs.task.EntityFunction
import com.glyph._scala.lib.util.pool.GlobalPool._
import MathUtils._
import Tint._
import com.glyph._scala.game.action_puzzle.view.APResource
import com.glyph._scala.lib.libgdx.actor.widgets.Center
import com.glyph._scala.game.action_puzzle.view.APResource
import com.glyph._scala.lib.libgdx.actor.widgets.Center

case class APResource(roundTex: Texture, particleTex: Texture, dummyTex: Texture, skin: Skin, fireTex: Texture, stopWatchTex: Texture,flareTex:Texture)
class ActionPuzzleTable(game: ComboPuzzle)(resource: APResource)
  extends Table
  with Reactor
  with Logging
  with Updating
  with Threading {
  var gameOverCallback: () => Unit = () => {}
  //TODO パーティクルをもっとキラキラさせる必要があるようだ。
  import game._
  import resource._
  val sceneMatrix = new Matrix4
  implicit val scene = new Scene
  val particleHolder = scene.createEntity()
  particleHolder += new Gravity <| (_.power = 1400)
  particleHolder += new AreaSensor(new Rectangle, e => e.remove())
  val trailRenderer = scene += new TrailRenderer(sceneMatrix, particleTex)
  val spriteRenderer = scene += new SpriteRenderer(sceneMatrix)
  val registerSprite:Sprite=>Unit = s => spriteRenderer.sprites += s
  val unregisterSprite:Sprite=>Unit = s=>spriteRenderer.sprites -= s
  scene += particleHolder
  val tmp = new Vector2
  // val renderer = new ParticleRenderer[MyTrail](particleTex)(ShaderHandler("shader/rotate2.vert", "shader/default.frag"), new BaseStripBatch(1000 * 10 * 2, UVTrail.ATTRIBUTES))
  // you must regenerate this after context loss...
  val corbert = FontUtil.internalFont("font/corbert.ttf", 100)
  val apView = new APView[Int, SpriteActor](game.puzzle)(new Pooling[SpriteActor] {
    override def newInstance: SpriteActor = new SpriteActor()
    override def reset(tgt: SpriteActor): Unit = {
      tgt.reset()
      tgt.sprite.setTexture(roundTex)
      tgt.sprite.asInstanceOf[TextureRegion].setRegion(0, 0, roundTex.getWidth, roundTex.getHeight)
    }
  }, classOf[SpriteActor])
    with Scoring[Int, SpriteActor]
    with Updating {
    override def font: BitmapFont = corbert
  }

  val easedScore = Eased(score map (_.toFloat), Interpolation.exp10Out.apply, _ => 2f)
  log("initializing a table")
  apView.addUpdatable(easedScore)
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
  val comboLabel = new RLabel(skin, heat.mapTo("%.1f".format(_)))
  val easedHeat = Eased(heat.mapTo(identity), Interpolation.exp10Out.apply, _ => 1f)
  addUpdatable(easedHeat)
  val heatGauge = new Table {
    val back = SpriteActor(dummyTex)
    add(back).fill.expand
    override def act(delta: Float): Unit = {
      super.act(delta)
      back.setWidth(easedHeat() / requiredHeat() * getWidth)
    }
  }
  val timerGauge = new Table {
    val back = SpriteActor(new Sprite(dummyTex))
    add(back).fill.expand
    time map (_ / 60f * getWidth) += back.setWidth
  }
  val inner = new Table()
  inner.debug
  inner.add(scoreLabel).expand
  inner.add(comboLabel).expand

  val tableSetup = (t: Table) => {
    t.defaults().fill.expand
    t.debug()
  }
  val logger = new LogView with Scissor{
    def addString(e:String){
      this << new Label(""+e,skin) with Log{
        override def dispose(): Unit = {}
      }
    }
    //events += ((e:PanelRemove)=>{addString(e.removed+"")})
    events += ((e:PanelPattern)=>{addString(e.kind)})
    events += addString
    setTouchable(Touchable.disabled)
  }
  val mainLayer = new Layered {}
  mainLayer.addActor(apView)
  mainLayer.addActor(logger)
  val heatTable = new Table() <| tableSetup
  val heatImage = (fireTex: Image) <| (_.setScaling(Scaling.fit))
  val levelLabel = new RLabel(skin, level.map(_.toString))
  heatTable.add(heatImage).width(0.1f.width)
  heatTable.add(Center(levelLabel)).width(0.1f.width)
  heatTable.add(heatGauge).width(0.8f.width)
  val timerTable = new Table() <| tableSetup
  val stopWatchImage = (stopWatchTex: Image) <| (_.setScaling(Scaling.fit))
  timerTable.add(stopWatchImage).width(0.1f.width)
  timerTable.add(timerGauge).width(0.9f.width)
  add(inner).fill.expandX.height((4f / 15f).height).row //.height(4f.height).row
  add(heatTable).fill.padLeft((0.01f).width).padRight((0.01f).width).expandX.height((1f / 15f).height).row
  add(mainLayer/*apView*/).fill.expandX.height((9f / 15f).height).row //.height(9.0f.height).row
  add(timerTable).fill.pad(0f.height, 0.01f.width, 0f.height, 0.01f.width).expandX.height((1f / 15f).height)
  game.onPanelAdd = apView.panelAdd
  game.onPanelRemove = seq => {
    apView.panelRemove(seq)
  }
  import MathUtils._
  preAlloc[Transform](1000)
  preAlloc[SpriteHolder](1000)
  preAlloc[Tint](1000)
  preAlloc[Velocities](1000)
  preAlloc[Expire](1000)
  preAlloc[EntityTaskProcessor](1000)
  val particleEmitter:Entity=>Entity = e => {
    val p = Template.particle(e.scene)
    val trans = e.component[Transform]
    val sprite = auto[SpriteHolder].setRegisters(registerSprite,unregisterSprite)
    val pTrans = p.component[Transform]
    val pTint = p.component[Tint]
    val pVel = p.component[Velocities]
    val duration = 1f
    pTint.color.set(e.component[Tint].color)
    pVel.ignoreGravity = true
    pVel.vel.set(0,random(500f)).rotate(random(360f))
    val processor = auto[EntityTaskProcessor]
    p += processor
    processor.addTask(Parallel(Interpolate(pTint.color) of Accessors.Color to (1,1,1,0) in duration using Interpolation.exp10Out))
    pTrans.matrix.set(trans.matrix)
    pTrans.matrix.rotate(random(360f))
    sprite.sprite.setTexture(flareTex)
    sprite.sprite.setRegion(0f,0f,1f,1f)
    val size = random(200f)
    sprite.sprite.setSize(size,size)
    p += sprite
    val expire = p += auto[Expire]
    expire.duration = duration
    p
  }
  game.onPanelScore = (p, s) => {//called when the panel is destroyed with score.
    val token = p.extra.asInstanceOf[Token[Int, SpriteActor]]
    //TODO why the hell is the effect not showing up?
    apView.popScore(token, s)
    var i = 0
    while (i < s ) {
      val trail = Template.trail
      tmp.set(token.getX + token.getWidth / 2, token.getY + token.getHeight / 2)
      apView.localToStageCoordinates(tmp)
      //init position and velocities
      trail.component[Transform].matrix.setToTranslation(tmp)
      val vels = trail.component[Velocities]
      vels.vel.set(1, 0).rotate(random(360f)).scl(random(500f,2000f))
      vels.viscosity = 0.002f
      vels.weight = 1f
      vels.ignoreGravity = true
      //gravity timer
      val processor = auto[EntityTaskProcessor]
      trail += processor
      trail.component[Tint].color.set(token.tgtActor.getColor)
      processor.addTask(Sequence(Delay(0.3f),Do(EntityFunction(trail,_.component[Velocities].ignoreGravity = false))))
      //sprite
      val sprite = auto[SpriteHolder].setRegisters(registerSprite,unregisterSprite)
      sprite.sprite.setTexture(particleTex)
      sprite.sprite.setRegion(0f,0f,1f,1f)
      sprite.sprite.setSize(20,20)
      trail += sprite
      //add emitter
      val pEmitter = auto[ParticleEmitter]
      pEmitter.setup(particleEmitter,0.016f)
      trail += pEmitter
      //add trail to the root entity
      particleHolder += trail
      i += 1
    }
    //apView.explodeToken(token,s/10)
  }
  game.onGameOverCallback = () => {
    val label = Center(new Label("Time Up", skin)) <| (_.setBackground(skin.getDrawable("emerald")))
    import Actions._
    import Interpolation._
    label.setSize(getWidth, getHeight / 5)

    val height = getHeight / 2 - label.getHeight / 2
    label.setPosition(getWidth, height)

    apView.removeAllToken()
    label.addAction(sequence(
      parallel(
        moveTo(getWidth / 2 - label.getWidth / 2, height, 0.5f, exp10Out),
        fadeIn(0.5f, exp10Out)
      ),
      fadeOut(1, exp10In),
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
    scene.update(delta)
    game.update(delta)
    super.act(delta)
  }


  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    sceneMatrix.set(getStage.getCamera.combined)
    batch.end()
    scene.draw()
    batch.begin()
  }

  override def layout(): Unit = {
    super.layout()
    val cx = heatImage.getWidth / 2 + heatImage.getX
    val cy = heatImage.getHeight / 2 + heatImage.getY
    heatImage.localToStageCoordinates(tmp.set(cx, cy))
    particleHolder.script[Gravity].center.set(tmp)
    heatImage.localToStageCoordinates(tmp.set(heatImage.getX, heatImage.getY))
    val ax = tmp.x
    val ay = tmp.y
    heatImage.localToStageCoordinates(tmp.set(heatImage.getX + heatImage.getWidth, heatImage.getY + heatImage.getHeight))
    particleHolder.script[AreaSensor].area.set(ax, ay, tmp.x - ax, tmp.y - ay)
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

  def animated(game: ComboPuzzle)(resources: APResource)(implicit processor: ParallelProcessor): AnimatedConstructor = {
    val view = new ActionPuzzleTable(game)(resources)
    info => callbacks =>
      val t = new AnimatedTable()
      t.add(view).fill.expand
      view.gameOverCallback = () => {
        callbacks("game_over")(Map("score" -> game.score()))
      }
      t
  }
}
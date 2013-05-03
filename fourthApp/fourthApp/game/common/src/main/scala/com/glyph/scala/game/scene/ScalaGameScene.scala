package com.glyph.scala.game.scene

import com.glyph.scala.Glyph
import com.badlogic.gdx.scenes.scene2d.Stage
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.{FPSLogger, Texture}
import com.glyph.libgdx.{Scene, Engine}
import com.glyph.libgdx.surface.Surface
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Skin, Table}
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.glyph.scala.Glyph.Timer
import com.glyph.scala.game.event.UIInputEvent
import com.glyph.scala.lib.engine.EntityPackage
import com.glyph.scala.game.factory.EntityFactory
import com.glyph.scala.game._
import component.Transform
import system.{CameraSystem, RenderSystem, DungeonSystem}
import ui.{Touchable, CardTable}
import com.glyph.libgdx.particle.{SpriteParticle, ParticlePool}

/**
 * a gamescene written in scala
 */
class ScalaGameScene(x: Int, y: Int) extends Scene(x, y) {
  /**
   * 設計思想：ゲームに必要なモデルクラスはGameContextにまとめるが、
   * 各モジュールには必要としている情報（インスタンス）のみ渡す様にする。
   * 疎結合！
   * GameContextのインスタンスを渡すことは極力避ける。
   * 渡している場合はリファクタリングできないか検討
   */
  val game = new GameContext
  val pkg = new EntityPackage("entity")
  val mFpsLogger = new FPSLogger
  val mGameTable = new Table
  val mGameStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT, true)
  val mSpritePool = new ParticlePool(classOf[SpriteParticle], 1000)
  val mCardTable = new CardTable(game.playerDeque, mSpritePool)
  lazy val renderSystem = new RenderSystem(game, pkg)
  lazy val factory = new EntityFactory(game, pkg)
  lazy val dungeonSystem = new DungeonSystem(game, pkg)
  lazy val cameraSystem = new CameraSystem(game, renderSystem.root)
  /**
   * initializer
   */
  Glyph.printExecTime("init views", {
    initViews()
  })
  Glyph.printExecTime("init systems", {
    renderSystem.setSize(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT / 2)
    game.systems.set(renderSystem)
    game.systems.set(dungeonSystem)
    game.systems.set(cameraSystem)
    game.systems.set(factory)
  })
  Glyph.printExecTime("init characters", {
    val c = factory.character()
    cameraSystem.setTarget(c.getMember[Transform].position)
    game.addEntity(c)
    game.addEntity(factory.dungeon())

  })

  /**
   * draw one card
   */
  for (i <- 0 until 10) {
    game.playerDeque.drawCard()
  }

  def initViews() {
    /**
     * init processors
     */
    addProcessor(mGameStage)
    addStage(mGameStage)

    /**
     * init skins
     */
    val skin = new Skin();
    val ra = AM.instance().get[Texture]("data/rightArrow.png");
    val la = AM.instance().get[Texture]("data/leftArrow.png");
    val exe = AM.instance().get[Texture]("data/lightbulb32.png");
    skin.add("right", ra);
    skin.add("left", la);
    skin.add("exec", exe);
    val bstyle = new ButtonStyle();
    bstyle.up = skin.getDrawable("right");
    skin.add("default", bstyle);

    /**
     * init buttons
     */

    val right = new Button(skin.getDrawable("right")) with Touchable;
    right.onPressing = () => {
      game.eventManager dispatch new UIInputEvent(UIInputEvent.RIGHT_BUTTON)
    }
    val left = new Button(skin.getDrawable("left")) with Touchable;
    left.onPressing = () => {
      game.eventManager dispatch new UIInputEvent(UIInputEvent.LEFT_BUTTON)
    }
    val exec = new Button(skin.getDrawable("exec")) with Touchable;
    exec.onPressing = () => {
      game.eventManager dispatch new UIInputEvent(UIInputEvent.EXEC_BUTTON)
    }


    val root = new Table
    root.setSize(Engine.VIRTUAL_WIDTH,Engine.VIRTUAL_HEIGHT)
    val t = mGameTable
    t.setSize(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT)
    t.row()
    t.add(renderSystem).expandX().height(Engine.VIRTUAL_HEIGHT / 2).fill()
      .colspan(3)
    t.row().fill()
    t.add(mCardTable).colspan(3).height(Engine.VIRTUAL_WIDTH / 5 * 1.618f * 2).expand(1, 3)
    t.row()
    t.add(left).expand(1, 1).height(100).fill()
    t.add(exec).expand(1, 1).fill()
    t.add(right).expand(1, 1).fill()
    t.debug()

    root.debug()
    root.top()
    root.add(t)
    mGameStage.addActor(root)


    mGameTable.layout()
  }

  val timer = new Timer(1000)

  override def render(delta: Float) {
    val et = Glyph.execTime {
      cameraSystem.onRenderFrame()
      super.render(delta)
      mFpsLogger.log();
      Table.drawDebug(mGameStage);
    }
    timer.repeat {
      val fps: Double = 1.0 / (et * 0.000000001)
      Glyph.printTime("elapsed:", et)
      Glyph.log("fps", "%.2f".format(fps));
    }

  }

}

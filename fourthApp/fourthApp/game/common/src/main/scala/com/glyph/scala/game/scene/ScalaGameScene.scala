package com.glyph.scala.game.scene

import com.glyph.scala.Glyph
import com.badlogic.gdx.scenes.scene2d.Stage
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.{FPSLogger, Texture}
import com.glyph.libgdx.{Scene, Engine}
import com.glyph.libgdx.surface.Surface
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.glyph.scala.Glyph.Timer
import com.glyph.scala.game.ui.UIButton
import com.glyph.scala.game.event.UIInputEvent
import com.glyph.scala.lib.engine.EntityPackage
import com.glyph.scala.game.factory.EntityFactory
import com.glyph.scala.game._
import component.{DTransform, Transform}
import com.glyph.scala.game.interface.renderer.{SimpleRenderer, Renderer}
import com.glyph.scala.game.interface.{ActorController, DungeonActor}
import system.{RenderSystem, DungeonSystem}

/**
 * a gamescene written in scala
 */
class ScalaGameScene(x: Int, y: Int) extends Scene(x, y) {
  val game = new GameContext
  val pkg = new EntityPackage("entity")
  val mFpsLogger = new FPSLogger
  val mGameTable = new Table
  val mGameSurface = new Surface(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT / 2)
  val mGameStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT, true)
  val mUIStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT, true)

  lazy val renderSystem = new RenderSystem(game, pkg)
  lazy val factory = new EntityFactory(game,pkg)
  lazy val dungeonSystem = new DungeonSystem(game, pkg)
  /**
   * initializer
   */
  Glyph.printExecTime("init views", {
    initViews()
  })
  Glyph.printExecTime("init systems", {
    renderSystem
    dungeonSystem
    mGameSurface.add(renderSystem)
    factory
  })
  Glyph.printExecTime("init characters", {
    game.addEntity(factory.character())
    game.addEntity(factory.dungeon())
  })

  def initViews() {
    /**
     * init processors
     */
    addProcessor(mGameStage)
    addProcessor(mUIStage)
    addStage(mGameStage)
    addStage(mUIStage)

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

    val right = new UIButton(skin.getDrawable("right"));
    right.onPressing = () => {
      game.eventManager dispatch new UIInputEvent(UIInputEvent.RIGHT_BUTTON)
    }
    val left = new UIButton(skin.getDrawable("left"));
    left.onPressing = () => {
      game.eventManager dispatch new UIInputEvent(UIInputEvent.LEFT_BUTTON)
    }
    val exec = new UIButton(skin.getDrawable("exec"));
    exec.onPressing = () => {
      game.eventManager dispatch new UIInputEvent(UIInputEvent.EXEC_BUTTON)
    }

    val t = mGameTable;
    t.setSize(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT);
    t.row();
    t.add(mGameSurface).expandX().height(Engine.VIRTUAL_HEIGHT / 2).fill()
      .colspan(3);
    t.row();
    t.add().colspan(3).expand(1, 3);
    t.row();
    t.add(left).expand(1, 1);
    t.add(exec).expand(1, 1);
    t.add(right).expand(1, 1);
    t.debug();
    mUIStage.addActor(t);
    mGameTable.layout();
  }

  val timer = new Timer(1000)

  override def render(delta: Float) {
    val et = Glyph.execTime {
      super.render(delta)
      mFpsLogger.log();
      mGameStage.act(delta)
      Table.drawDebug(mGameStage);
      Table.drawDebug(mUIStage);
      mGameSurface.resize()
    }
    timer.repeat {
      val fps: Double = 1.0 / (et * 0.000000001)
      Glyph.printTime("elapsed:", et)
      Glyph.log("fps", "%.2f".format(fps));
    }

  }
}

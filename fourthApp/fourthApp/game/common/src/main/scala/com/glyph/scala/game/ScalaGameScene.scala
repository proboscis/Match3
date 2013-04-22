package com.glyph.scala.game

import com.glyph.scala.Glyph
import com.badlogic.gdx.scenes.scene2d.Stage
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.{FPSLogger, Texture}
import event.UIInputEvent
import com.glyph.libgdx.{Scene, Engine}
import com.glyph.libgdx.surface.Surface
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import ui.UIButton
import com.glyph.scala.Glyph.Timer
import com.glyph.scala.lib.entity_property_system.test.Test
import com.glyph.scala.lib.math.Vec2
import com.glyph.scala.test.TableTest

/**
 * a gamescene written in scala
 */
class ScalaGameScene(x: Int, y: Int) extends Scene(x, y) {
  val game = new ScalaGameWorld
  val mFpsLogger = new FPSLogger
  val mGameTable = new Table
  val mGameSurface = new Surface(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT / 2)
  val mGameStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT, true)
  val mUIStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT, true)

  /**
   * initializer
   */
  Glyph.printExecTime("init", {
    initViews()

    /**
     * init systems
     */


    /**
     * init renderer
     */


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

  //val test = new Test
  val test = new TableTest

  override def render(delta: Float) {
    val et = Glyph.execTime {
      super.render(delta)
      mFpsLogger.log();
      mGameStage.act(delta)
     // Table.drawDebug(mGameStage);
     // Table.drawDebug(mUIStage);
      mGameSurface.resize()
    }
    timer.repeat {
      val fps: Double = 1.0 / (et * 0.000000001)
      Glyph.printTime("elapsed:", et)
      Glyph.log("fps", "%.2f".format(fps));
    }

  }
}

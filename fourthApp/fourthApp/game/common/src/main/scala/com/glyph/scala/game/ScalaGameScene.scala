package com.glyph.scala.game

import adapter.RendererAdapter
import com.glyph.scala.Glyph
import com.badlogic.gdx.scenes.scene2d.{Stage, InputEvent, InputListener}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.{FPSLogger, Texture}
import event.UIInputEvent
import system._
import com.glyph.libgdx.{Scene, Engine}
import com.glyph.libgdx.surface.Surface
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Skin, Table}
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.glyph.scala.lib.entity_component_system.GameContext
import ui.UIButton
import com.glyph.scala.Glyph.{Timer}
import annotation.tailrec
import scala.Some
import com.glyph.scala.lib.util.Maybe

/**
 * a gamescene written in scala
 */
class ScalaGameScene(x: Int, y: Int) extends Scene(x, y) {
  val game = new GameContext
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

    game.systemManager.addSystem(new ControllerSystem(game))
    game.systemManager.addSystem(new TagSystem(game))
    game.systemManager.addSystem(new DungeonSystem(game))
    game.systemManager.addSystem(new PlayerCameraSystem(game,mGameSurface.getCamera))


    /**
     * init renderer
     */
    mGameSurface.add(new RenderSystem(game.entityManager))

    /**
     * add adapters
     */
    game.entityManager.addAdapter[RendererAdapter]

    /**
     * init entities
     */
    Glyph.printExecTime("initEntity", {
      val dungeon = EntityFactory.createDungeon
      game.entityManager.addEntity(dungeon)

      for (i <- 1 to 1000) {
        val e = EntityFactory.createNewCharacter
       // game.entityManager.addEntity(e)
      }
      val player = EntityFactory.createPlayer
      game.entityManager.addEntity(player)
    })

  })

  def initViews (){
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

    val opt = Option(1)

    Glyph.printExecTime("for loop", {
      for( i<- 1 to 10000){

      }
    })
    Glyph.printExecTime("while loop", {
      var ii = 10000
      while (ii > 0) {
        ii = ii - 1
      }
    })
    Glyph.printExecTime("glyph loop", {
      Glyph.loop(10000,{
        _=>
      })
    })
    Glyph.printExecTime("tail loop", {
      @tailrec
      def loop(n:Int):Unit={
        if(n < 10000){
          loop(n+1)
        }
      }
      loop(0)
    })
    Glyph.printExecTime("opt match", {
      var ii = 10000
      while (ii > 0) {
        ii = ii - 1
        opt match {
          case Some(x) =>
          case None =>
        }
      }
    })
    Glyph.printExecTime("if", {
      var ii = 10000
      while (ii > 0) {
        ii = ii - 1
        val a = 1 == 2
        if(a){}
      }
    })
    val maybe = new Maybe(3)
    Glyph.printExecTime("maybe isNull", {
      var ii = 10000
      while (ii > 0){
        ii = ii - 1
        maybe.isNull
      }
    })
    Glyph.printExecTime("maybe checkNull", {
      var ii = 10000
      while (ii > 0){
        ii = ii - 1
        maybe.checkNull
      }
    })
    Glyph.printExecTime("maybe ?", {
      var ii = 10000
      while (ii > 0){
        ii = ii - 1
        maybe.?{i=>i}
      }
    })
    Glyph.printExecTime("opt defined", {
      var ii = 10000
      while (ii > 0) {
        ii = ii - 1
        if(opt.isDefined){
        }
      }
    })
    val opt2 = 1
    Glyph.printExecTime("match2", {
      var ii = 10000
      while (ii > 0) {
        ii = ii - 1
        opt2 match {
          case 2 =>
          case _ =>
        }
      }
    })
    val n = null
    Glyph.printExecTime("null check", {
      var ii = 10000
      while (ii > 0) {
        ii = ii - 1
        if (n == null) {
        } else {
        }
      }
    })
    new Test
  }

  val timer = new Timer(1000)


  override def render(delta: Float) {
    super.render(delta)
    val et = Glyph.execTime {
      mFpsLogger.log();
      mGameStage.act(delta)
      Table.drawDebug(mGameStage);
      Table.drawDebug(mUIStage);
      game.update(delta)
      mGameSurface.resize()
    }
    timer.repeat {
      val fps: Double = 1.0 / (et * 0.000000001)
      Glyph.log("fps", "%.2f".format(fps));
    }
  }
}

package com.glyph.scala.game.action_puzzle.debug

import com.glyph.scala.lib.libgdx.game.ConfiguredGame
import com.badlogic.gdx._
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.{Group, Actor, Stage}
import com.badlogic.gdx.graphics.{Color, GL10}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.screen.{ScreenBuilder, ConfiguredScreen, LoadingScreen}
import com.glyph.scala.game.action_puzzle.view.ActionPuzzleTable
import com.badlogic.gdx.math.{Vector3, Rectangle}
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.glyph.scala.lib.libgdx.actor.{SpriteActor, ActorUtil}
import com.badlogic.gdx.graphics.g2d.{Sprite, Batch, TextureRegion}
import com.glyph.scala.lib.util.gl.ViewportStack
import com.glyph.scala.game.action_puzzle.{MyTrail, APView, Token, ActionPuzzle}
import com.glyph.scala.lib.libgdx.game.ApplicationConfig
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.utils.NumberUtils
import com.glyph.scala.lib.util.pool.Pool

/**
 * @author glyph
 */
class Debugger extends Game with AssetManagerSupport with ConfiguredGame {
  val (h, w) = (1920 / 2, (1920 / 2 * 9d / 16d).toInt)
  implicit val assetManager = new AssetManager

  override def deskTopConfig: ApplicationConfig = ApplicationConfig(w * 3, h)
  val builder = new ScreenBuilder {
    def requirements: Set[(Class[_], Seq[String])] = ActionPuzzleTable.requiredAssets

    def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
      override def STAGE_WIDTH: Int = w * 2
      override def STAGE_HEIGHT: Int = h
      val skin: Skin = "skin/holo/Holo-dark-xhdpi.json".fromAssets
      val table = new ActionPuzzleTable
      val table2 = new Table()
      val hashColor = (t:Any) => new Color(NumberUtils.floatToIntColor(t.hashCode()))
      //val nextColor = (p:ActionPuzzle[Int]#AP) => new Color(p.next().map(p=>NumberUtils.floatToIntColor(p.hashCode())).getOrElse(Color.rgba8888(1,1,1,1)))
      table.game.puzzle.falling -> "falling" ::
        table.game.puzzle.fixed -> "fixed" ::
        table.game.puzzle.future -> "future"::
        table.game.puzzle.falling -> "next" :: Nil map {
        //case (buf, "next")=>new PuzzleBufferView(table.view, buf,nextColor) -> new Label("next", skin)
        case (buf, str) => new PuzzleBufferView(table.view, buf,hashColor) -> new Label(str, skin)
      } foreach {
        case (view, label) => {
          label.setFontScale(0.4f)
          table2.add(view).size(h / 5, h / 5).fill.expand.row
          table2.add(label).row
        }
      }
      table.setSize(w, h)
      root.add(table).size(w, h).fill.expand()
      root.add(table2).size(w, h).fill.expand()
      root.debug()
      debug() = true
    }
      }
  val resetProcessor = new InputAdapter {
    override def keyDown(keycode: Int): Boolean = {
      keycode match {
        case Keys.R => loadAndSetScreen(); true
        case _ => super.keyDown(keycode)
      }
    }
  }

  def create(): Unit = loadAndSetScreen()

  def loadAndSetScreen() {
    loadAssets(assetManager, ActionPuzzleTable.requiredAssets, () => {
      setScreen(builder.create(assetManager))
      val processor = Gdx.input.getInputProcessor
      val multiplexer = new InputMultiplexer()
      multiplexer.addProcessor(processor)
      multiplexer.addProcessor(resetProcessor)
      Gdx.input.setInputProcessor(multiplexer)
    })
  }
}

class PuzzleBufferView[A<:Actor](
                        view: APView[Int,A],
                        tgt: ActionPuzzle[Int]#PuzzleBuffer,
                        colorFunc:ActionPuzzle[Int]#AP => Color)
                      (implicit assets: AssetManager) extends Group {
  implicit val spritePool = Pool[Sprite](100)
  val sprite = manual[Sprite]
  sprite.setTexture("data/dummy.png".fromAssets)
  sprite.asInstanceOf[TextureRegion].setRegion(0, 0, sprite.getTexture.getWidth, sprite.getTexture.getHeight)
  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    for {
      (fx, fy) <- view.gridFunctions()
    } {
      //log("draw",getWidth,getHeight)
      var x = 0
      val width = tgt.length
      while (x < width) {
        var y = 0
        val row = tgt(x)
        val height = row.length
        while (y < height) {
          sprite.setPosition(fx.indexToAlpha(x) * getWidth + getX, fy.indexToAlpha(y) * getHeight + getY)
          sprite.setSize(fx.tokenSize * getWidth, fy.tokenSize * getHeight)
          //sprite.setColor(Token.colorMap(tgt(x)(y).debugState)())
          sprite.setColor(colorFunc(tgt(x)(y)))
          sprite.draw(batch)
          y += 1
        }
        x += 1
      }
    }
  }
}
class StagedTable(width: Int, height: Int) extends Widget with Logging {
  val stage = new Stage(width, height, true)
  val area, screenArea = new Rectangle

  val hitTmp = new Vector3

  override def hit(x: Float, y: Float, touchable: Boolean): Actor = {
    //super.hit(x, y, touchable)
    hitTmp.set(x, y, 0)
    log("before", hitTmp)
    log("after", hitTmp)
    stage.getRoot.hit(hitTmp.x / 2, hitTmp.y / 2, touchable)
    //super.hit(x,y,touchable)
  }


  override def act(delta: Float): Unit = {
    super.act(delta)
    stage.act(delta)
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    setupInnerStageViewport()
    super.draw(batch, parentAlpha)
    batch.end()
    ViewportStack.push(screenArea)
    stage.draw()
    Table.drawDebug(stage)
    ViewportStack.pop()
    batch.begin()
  }

  def setupInnerStageViewport() {
    log(Gdx.graphics.getWidth, Gdx.graphics.getHeight)
    area.set(getX, getY, getWidth, getHeight)
    ActorUtil.getBounds(getStage.getCamera)(area)(screenArea)(getStage.getSpriteBatch.getTransformMatrix)
    stage.setViewport(width, height, true, screenArea.x, screenArea.y, screenArea.width, screenArea.height)
    //stage.getCamera.translate(-stage.getGutterWidth,-stage.getGutterHeight, 0)
  }
}

trait AssetManagerSupport {
  self: Game =>
  def loadAssets(assetManager: AssetManager, assets: Set[(Class[_], Seq[String])], callback: () => Unit) {
    assets foreach {
      case (clazz, files) => files foreach (assetManager.load(_, clazz))
    }
    setScreen(new LoadingScreen(() => {
      setScreen(null)
      callback()
    }, assetManager))
  }
}


trait TabledGame extends Game {
  lazy val stage = new Stage()
  lazy val root = new Table

  def create(): Unit = {
    Gdx.input.setInputProcessor(stage)
    root setFillParent true
    stage addActor root
  }

  override def resize(width: Int, height: Int): Unit = {
    super.resize(width, height)
    stage.setViewport(width, height, true)
  }

  override def render(): Unit = {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    super.render()

    stage.act(Gdx.graphics.getDeltaTime)
    stage.draw()
    Table.drawDebug(stage)
  }

  override def dispose(): Unit = {
    super.dispose()
    stage.dispose()
  }
}

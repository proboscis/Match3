package com.glyph.scala.game.action_puzzle.debug

import com.glyph.scala.lib.libgdx.game.{ApplicationConfig, ScreenBuilderSupport, ConfiguredGame}
import com.badlogic.gdx.{InputMultiplexer, Gdx, Game}
import com.badlogic.gdx.scenes.scene2d.ui.{Widget, WidgetGroup, Table}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.{Color, Texture, GL10}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, LoadingScreen}
import com.glyph.scala.game.action_puzzle.view.{ActionPuzzleTableScreen, ActionPuzzleTable}
import com.badlogic.gdx.math.{Matrix4, Rectangle, Vector2}
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.glyph.scala.lib.libgdx.actor.{ActorUtil, SpriteActor}
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import scala.concurrent.ops
import com.glyph.scala.lib.util.gl.ViewportStack

/**
 * @author glyph
 */
class Debugger extends Game with AssetManagerSupport with ConfiguredGame{
//I think I need some refucktoring
  override def deskTopConfig: ApplicationConfig = ApplicationConfig(1920,1080)

  def create(): Unit = {
    implicit val assetManager = new AssetManager
    loadAssets(assetManager,ActionPuzzleTable.requiredAssets,()=>{
      setScreen(new ConfiguredScreen {

        override def STAGE_WIDTH: Int = 1920
        override def STAGE_HEIGHT: Int = 1080

        val (w,h)=(1920/3,(1920 / 3 * 9d / 16d).toInt)

        val table = new ActionPuzzleTable(assetManager,1920/2,1080)

        val staged = new StagedTable(w,h)
        val tex:Texture = "data/dummy.png".fromAssets
        val spriteActor = new SpriteActor

        spriteActor.sprite.setTexture(tex)
        spriteActor.sprite.asInstanceOf[TextureRegion].setRegion(0,0,tex.getWidth,tex.getHeight)
        spriteActor.setSize(50,50)
        spriteActor.setColor(Color.WHITE)
        table.setSize(w,h)
        staged.stage.addActor(table)
        staged.stage.addActor(spriteActor)
        root.add(staged).fill.expand
        root.debug()
        debug() = true

      })
    })
  }
}
class StagedTable(width:Int,height:Int) extends Widget with Logging{
  val stage = new Stage(width,height,true)
  val area,screenArea = new Rectangle
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

  def setupInnerStageViewport(){
    log(Gdx.graphics.getWidth,Gdx.graphics.getHeight)
    area.set(getX,getY,getWidth,getHeight)
    ActorUtil.getBounds(getStage.getCamera)(area)(screenArea)(getStage.getSpriteBatch.getTransformMatrix)
    stage.setViewport(width,height,true,screenArea.x,screenArea.y,screenArea.width,screenArea.height)
    stage.getCamera.translate(-stage.getGutterWidth,-stage.getGutterHeight, 0)
  }
}

trait AssetManagerSupport {
  self:Game =>
  def loadAssets(assetManager:AssetManager,assets:Set[(Class[_],Seq[String])],callback:()=>Unit){
    assets foreach{
      case (clazz,files) => files foreach (assetManager.load(_,clazz))
    }
    setScreen(new LoadingScreen(()=>{
      setScreen(null)
      callback()
    },assetManager))
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

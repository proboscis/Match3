package com.glyph.scala.game.action_puzzle.debug

import com.glyph.scala.lib.libgdx.game.{ApplicationConfig, ScreenBuilderSupport, ConfiguredGame}
import com.badlogic.gdx.{Gdx, Game}
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, LoadingScreen}
import com.glyph.scala.game.action_puzzle.view.{ActionPuzzleTableScreen, ActionPuzzleTable}

/**
 * @author glyph
 */
class Debugger extends Game with AssetManagerSupport with ConfiguredGame{
//I think I need some refucktoring
  override def deskTopConfig: ApplicationConfig = ApplicationConfig(1920,1080)

  def create(): Unit = {
    val assetManager = new AssetManager
    loadAssets(assetManager,ActionPuzzleTable.requiredAssets,()=>{
      setScreen(new ConfiguredScreen {

        override def STAGE_WIDTH: Int = 1920


        override def STAGE_HEIGHT: Int = 1080

        val (w,h)=(1920/3,(1920 / 3 * 9d / 16d).toInt)

        val table = new ActionPuzzleTable(assetManager,w,h)
        table.setFillParent(true)
        root.add(table).size(w,h)
        root.invalidate()
        root.layout()
      })
    })
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

object AssetUtil {
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

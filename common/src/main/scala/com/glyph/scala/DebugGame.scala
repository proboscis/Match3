package com.glyph.scala

import com.badlogic.gdx._
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.{GL10, Texture}
import game.puzzle.screen.PuzzleScreen
import lib.libgdx.screen.LoadingScreen
import lib.util.MemoryAnalyzer
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * @author glyph
 */
class DebugGame extends Game {
  var pausedScreen: Option[Screen] = None

  override def resume() {
    super.resume()
    println("resume!")
    setScreen(new LoadingScreen (()=>{
      pausedScreen foreach setScreen
    }))
  }


  override def setScreen(screen: Screen) {
    println("setScreen:"+screen)
    super.setScreen(screen)
  }

  override def pause() {
    super.pause()
    println("pause!")
    pausedScreen = Some(getScreen)
  }

  def create() {
    Gdx.app.log("DebugGame", "CreatedDebugGame")
    new MemoryAnalyzer
    //new ReactiveMonitor
    AM.create()
    var i = 1
    /*
    while (i <= 10) {
      AM.instance().load("data/card" + i + ".png", classOf[Texture])
      i += 1
    }*/
    val am = AM.instance()
    am.load("skin/default.json",classOf[Skin])
    am.finishLoading()
    //TODO make loading screen appear fast !
    am.load("data/particle.png", classOf[Texture])
    am.load("data/sword.png",classOf[Texture])
    am.load("data/dummy.png",classOf[Texture])

    //    AM.instance().load("sound/drawcard.mp3", classOf[Sound])
    //    AM.instance().load("sound/gore.wav", classOf[Sound])
    //    AM.instance().load("data/background.png", classOf[Texture])
    //    AM.instance().load("data/table.png", classOf[Texture])
    //    AM.instance().load("data/tile.png", classOf[Texture])
    //    AM.instance().load("data/rightArrow.png", classOf[Texture])
    //    AM.instance().load("data/leftArrow.png", classOf[Texture])
    //
    //    AM.instance().load("data/TileA4.png", classOf[Texture])
    //    AM.instance().load("data/skeleton.png", classOf[Texture])
    //    AM.instance().load("data/lightbulb32.png", classOf[Texture])
    //AM.instance().finishLoading()
    setScreen(new LoadingScreen(()=>{
      println("load done")
      //setScreen(new DebugScreen)
      //setScreen(new ScrollTestScreen)
      setScreenConstructor(new PuzzleScreen)
    }))
  }

  def setScreenConstructor(f: => Screen) {
    Gdx.input.setInputProcessor(null)
    //val usedVars = Var.allVariables
    setScreen(f)//ここでprocessorがセットされるなければならない
    val currentProcessor = Gdx.input.getInputProcessor
    val multiplexer = new InputMultiplexer()
    multiplexer.addProcessor(new InputAdapter {
      override def keyDown(keycode: Int): Boolean = keycode match {
        case Input.Keys.R => setScreenConstructor(f); true
        case _ => false
      }
    })
    multiplexer.addProcessor(currentProcessor)
    Gdx.input.setInputProcessor(multiplexer)
    //System.gc()//notify the vm that this is a good time to do this.
    /*
    new Thread(new Runnable {
      def run() {
        Thread.sleep(10000)
        usedVars foreach {
          case WeakReference(ref) => println("leaked ref:"+ref)
          case any => println("lost ref")
        }
      }
    }).start()
    */
  }

  override def render() {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    super.render()
  }
}

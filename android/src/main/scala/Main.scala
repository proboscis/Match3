package com.glyph

import android.os.Bundle
import com.badlogic.gdx.backends.android._
import com.glyph.scala.lib.libgdx.game.{ScreenTester, ScreenGame}
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.Gdx
import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.lib.libgdx.screen.ScreenConfig
import com.glyph.scala.game.action_puzzle.screen.ActionScreen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.glyph.scala.lib.libgdx.game.ScreenFileTester
import com.glyph.scala.test.TestRunner

class Main extends AndroidApplication with Logging{
  override def onCreate(savedInstanceState: Bundle) {
    val actionScreenConfig = ScreenConfig(classOf[ActionScreen], Set(classOf[Texture] -> Array(
      "data/dummy.png",
      "data/particle.png",
      "data/sword.png",
      "data/round_rect.png"),
    classOf[Skin] -> Array("skin/default.json")))
    System.err.println(ScreenBuilder.writeConfig(actionScreenConfig))
  //this prints nothing but a "{}"



    println("created android application")
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = true
    config.useCompass = false
    config.useWakelock = true
    config.useGL20 = true
    
    ScreenBuilder.configToBuilder(actionScreenConfig) match {
      case Success(s) =>    new Thread(Thread.currentThread().getThreadGroup, new Runnable {
        def run() {
          //initialize(new ScreenFileTester("screens/action.js"),config)
             // initialize(new ScreenTester("com.glyph.scala.test.ComboEffect"), config)
          initialize(new TestRunner, config)

        }
        }, Thread.currentThread().getName + "logic", 64000000).run()
      case Failure(e) => e foreach(_.printStackTrace())
    }
  }
}

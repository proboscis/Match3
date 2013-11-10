package com.glyph

import android.os.Bundle
import com.badlogic.gdx.backends.android._
import com.glyph.scala.lib.libgdx.game.{ScreenTester, ScreenGame}
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.Gdx
import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder.ScreenConfig
import com.glyph.scala.game.action_puzzle.screen.ActionScreen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class Main extends AndroidApplication with Logging{
  override def onCreate(savedInstanceState: Bundle) {
    println("created android application")
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = false
    config.useCompass = false
    config.useWakelock = true
    config.useGL20 = true
    val actionScreenConfig = ScreenConfig(classOf[ActionScreen],Set(classOf[Texture]->Array(
      "data/dummy.png",
      "data/particle.png",
      "data/sword.png"),
      classOf[Skin]->Array("skin/default.json")))
    new Thread(Thread.currentThread().getThreadGroup, new Runnable {
      def run() {
        val builderVnel = ScreenBuilder.configToBuilder(actionScreenConfig)
        builderVnel match {
          case Failure(error) => err
          case Success(builder)=>initialize(new ScreenTester(builder), config)
        }

      }
    }, Thread.currentThread().getName + "logic", 64000000).run()
  }
}

package com.glyph._scala.lib.libgdx.game

import com.glyph._scala.lib.libgdx.screen.{LoadingScreen, ScreenBuilder}
import com.badlogic.gdx.{Screen, Gdx}
import com.glyph._scala.lib.libgdx.{Builder, GdxUtil}

import com.glyph._scala.game.Glyphs
import Glyphs._
/**
 * @author glyph
 */
trait ScreenBuilderSupport extends ReloadOnPause {
  def setBuilder(builder: Builder[Screen]) {
    //if all resources are ready
    implicit val am = assetManager
    if (builder.isReady) {
      //set screen immediately
      setScreen(builder.create(assetManager))
    } else {
      // enqueue resources and set loading screen
      assetManager.load(builder.requirements)
      setScreen(new LoadingScreen(() => {
        val screen = builder.create(assetManager)
        System.gc()
        GdxUtil.post{
          setScreen(screen)
        }
      }, assetManager))
    }
  }
}


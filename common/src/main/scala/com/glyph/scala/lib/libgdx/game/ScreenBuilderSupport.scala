package com.glyph.scala.lib.libgdx.game

import com.glyph.scala.lib.libgdx.screen.{LoadingScreen, ScreenBuilder}
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.libgdx.GdxUtil

/**
 * @author glyph
 */
trait ScreenBuilderSupport extends ReloadOnPause {
  def setBuilder(builder: ScreenBuilder) {
    //if all resources are ready
    if (builder.requirements forall {
      case (clazz, fileNames) => fileNames forall {
        assetManager.isLoaded(_, clazz)
      }
    }) {
      //set screen immediately
      setScreen(builder.create(assetManager))
    } else {
      // enqueue resources and set loading screen
      builder.requirements foreach {
        case (clazz, fileNames) => fileNames foreach {
          assetManager.load(_, clazz)
        }
      }
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


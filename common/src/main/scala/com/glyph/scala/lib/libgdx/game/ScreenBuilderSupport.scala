package com.glyph.scala.lib.libgdx.game

import com.glyph.scala.lib.libgdx.screen.{LoadingScreen, ScreenBuilder}

/**
 * @author glyph
 */
trait ScreenBuilderSupport extends ReloadOnPause {
  def setBuilder(builder: ScreenBuilder) {
    //if all resources are ready
    if (builder.requiredAssets forall {
      case (clazz, fileNames) => fileNames forall {
        assetManager.isLoaded(_, clazz)
      }
    }) {
      //set screen immediately
      setScreen(builder.create(assetManager))
    } else {
      // enqueue resources and set loading screen
      builder.requiredAssets foreach {
        case (clazz, fileNames) => fileNames foreach {
          assetManager.load(_, clazz)
        }
      }
      setScreen(new LoadingScreen(() => {
        System.gc()
        setScreen(builder.create(assetManager))
      }, assetManager))
    }
  }
}


package com.glyph.scala.lib.libgdx.game

import com.glyph.scala.lib.libgdx.screen.{LoadingScreen, ScreenBuilder}

/**
 * @author glyph
 */
trait ScreenBuilderSupport extends ReloadOnPause {
  def setBuilder(builder: ScreenBuilder) {
    if (builder.requiredAssets forall {
      case (fileName, clazz) => assetManager.isLoaded(fileName, clazz)
    }) {
      setScreen(builder.create(assetManager))
    } else {
      setScreen(new LoadingScreen(() => {
        setScreen(builder.create(assetManager))
      }, assetManager))
    }
  }
}

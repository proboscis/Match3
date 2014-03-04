package com.glyph._scala.test

import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
class GameResultTest extends MockTransition {
  override implicit def assetManager: AssetManager = new AssetManager()
  manager.start(result, Map("score" -> 1000), holder.push)
}

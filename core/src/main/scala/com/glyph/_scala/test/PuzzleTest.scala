package com.glyph._scala.test

import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.game.action_puzzle.view.animated.LazyAssets

/**
 * @author glyph
 */
class PuzzleTest extends MockTransition with LazyAssets{
  manager.start(puzzle,Map(),holder.push)
}
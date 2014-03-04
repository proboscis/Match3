package com.glyph._scala.test

import com.glyph._scala.game.action_puzzle.view.animated.{GameResult, LazyAssets}
import com.glyph._scala.lib.util.reactive.{VClass, Reactor}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager._

/**
 * @author glyph
 */
class GameResultMockTest extends MockTransition with LazyAssets with Reactor{
  manager.start(resultMock,Map(),holder.push)
  log("created GameResultMockTest")
  reactVar(VClass[AnimatedConstructor,GameResult]){
    t =>
      //ok, some how this is called twice.. regardless of animations
      log("changed")
      err(t)
  }
}

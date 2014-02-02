package com.glyph.scala.lib.libgdx.actor.transition

import com.glyph.scala.lib.util.Animated
import com.badlogic.gdx.scenes.scene2d.Actor
import AnimatedManager._
import com.glyph.scala.lib.libgdx.{BuilderExtractor, Builder}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor

/**
 * i dont want the screen to care anything about transitioning.?
 */
/**
 * well, this is useless since this forces some class to manage whole scene transitions!
 * @param builderMap
 * @param assets
 */
class AnimatedManager
(builderMap: Map[Builder[AnimatedConstructor], Map[String, (Builder[AnimatedActor] => Unit, Builder[AnimatedConstructor])]])
(implicit assets: AssetManager)
  extends BuilderExtractor {
  def start(builder: Builder[AnimatedConstructor], info: Info, transit: Builder[AnimatedActor] => Unit) {
    val callbacks = builderMap(builder).mapValues {
      case (transitioner, constructorBuilder) =>
        (info: Info) => start(constructorBuilder, info, transitioner)
    }
    val animatedBuilder = builder map (_(info)(callbacks))
    transit(animatedBuilder)
  }
}

object AnimatedManager {
  type Info = String Map Any
  type Callback = Info => Unit
  type Callbacks = String Map Callback
  type AnimatedConstructor = Info => Callbacks => Actor with Animated
}
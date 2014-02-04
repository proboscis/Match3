package com.glyph.scala.lib.libgdx.actor.transition

import com.glyph.scala.lib.util.Animated
import com.badlogic.gdx.scenes.scene2d.Actor
import AnimatedManager._
import com.glyph.scala.lib.libgdx.{BuilderExtractor, Builder}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor


//TODO make this manager don't handle the builder and initializer and let the animated itself to handle it.
class AnimatedManager
(builderMap: Map[Builder[AnimatedConstructor], Map[String, (Builder[AnimatedActor] => Unit, Builder[AnimatedConstructor])]])
(implicit assets: AssetManager)
  extends BuilderExtractor {
  val builders = builderMap withDefaultValue Map()
  def start(builder: Builder[AnimatedConstructor], info: Info, transit: Builder[AnimatedActor] => Unit) {
    val callbacks:Map[String,Info=>Unit] = builders(builder).mapValues {
      case (transitioner, constructorBuilder) =>
        (info: Info) => start(constructorBuilder, info, transitioner)
    }.withDefault(_=>(info:Info)=>Unit)
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
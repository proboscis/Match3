package com.glyph._scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui._
import scala.language.existentials
import scala.collection.mutable
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.util.updatable.task.{ParallelProcessor, Block, Sequence, Delay}
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.glyph._scala.lib.libgdx.actor.{Tasking, AnimatedTable}
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.test.AnimatedRunner
import com.badlogic.gdx.assets.AssetManager

object Title {
  case class TitleStyle(
                         margin: Float = 20,
                         space: Float = 10,
                         titleFont: BitmapFont,
                         roundTex: Texture,
                         skin: Skin
                         )
  def third(style: TitleStyle)(implicit processor:ParallelProcessor): AnimatedConstructor = info => callbacks => new AnimatedTable with Tasking {
    setSkin(style.skin)
    add(new Label("this is title", style.skin)).fill.expand
    add(Sequence(Delay(1f), Block {
      callbacks lift "dummy" foreach (_(Map()))
    }))
  }
}

trait LazyAssets extends AnimatedRunner {
  // you have to make this before this instance.. making this instance lazy solved the problem,
  // but i'm afraid this will cause some other problems...
  lazy val am = new AssetManager

  //beware of second asset manager!
  override implicit def assetManager: AssetManager = am
}

class WaitCallback(onComplete: () => Unit) {
  val callbackFlag = new mutable.HashMap[AnyRef, Boolean]() withDefault (_ => false)

  def token[P, R](f: P => R): P => R = param => {
    callbackFlag(f) = true
    if (callbackFlag.values.forall(identity)) {
      onComplete
    }
    f(param)
  }
}
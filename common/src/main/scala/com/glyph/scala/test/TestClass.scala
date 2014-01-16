package com.glyph.scala.test

import com.glyph.scala.game.action_puzzle.view.{TitleBuilder, ActionPuzzleTable, ActionPuzzleTableScreen}
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder._
import scalaz.Success
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.util.reactive.{VClass, Varying}
import scala.reflect.ClassTag
import com.glyph.scala.lib.libgdx.Builder
import com.glyph.scala.lib.util.Animated
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
object TestClass {
  type ->[A, B] = (A, B)
  val builderClasses: List[Class[_ <: ScreenBuilder]] =
    classOf[ActionPuzzleTableScreen] ::
    classOf[TransformFeedback]::
      classOf[VaryingScreen]::
      classOf[AnimatedHolderTest] ::
      classOf[ActorHolderTest] ::
      classOf[TrailedParticleTest] ::
      classOf[ParticleTest] ::
      classOf[UVTrailTest] ::
      classOf[ImmediateTest] ::
      classOf[WordParticle] :: Nil
  val files = "screens/action.js" :: "screens/puzzle.js" :: Nil
  val screenClasses: List[Class[_ <: Screen]] =
    classOf[ShaderRotationTest] ::
      classOf[ExplosionTest] ::
      classOf[MeshTest] ::
      classOf[TrailTest] ::
      classOf[EffectTest] ::
      classOf[FrameBufferTest] ::
      classOf[WindowTest] ::
      classOf[WindowTest2] ::
      Nil
  val widgetGroupClasses: List[Class[_ <: WidgetGroup]] =
    classOf[ActionPuzzleTable] :: Nil
  //Classes are not covariant
  val animatedClasses:List[Class[_ <: Builder[Actor with Animated]]] =
    classOf[TitleBuilder]::Nil
  val classNameSet = animatedClasses ++ builderClasses ++ screenClasses map (s =>s->s.getSimpleName)
  val classBuilders = builderClasses map (c => c.newInstance() -> c.getSimpleName)
  val fileBuilders = files map {
    f => createFromJson(f) -> f
  } collect {
    case (Success(s), f) => s -> f
  }
  val pkgBuilders = screenClasses map {
    clazz => new ScreenBuilder {
      def requirements: Set[(Class[_], Seq[String])] = Set()

      def create(implicit assetManager: AssetManager): Screen = clazz.newInstance()
    } -> clazz.getSimpleName
  }
  val builders: Seq[ScreenBuilder -> String] = classBuilders ++ pkgBuilders ++ fileBuilders
}

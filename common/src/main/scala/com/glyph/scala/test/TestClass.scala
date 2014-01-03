package com.glyph.scala.test

import com.glyph.scala.game.action_puzzle.view.{ActionPuzzleTable, ActionPuzzleTableScreen}
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder._
import scalaz.Success
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup

/**
 * @author glyph
 */
object TestClass {
  type ->[A, B] = (A, B)

  val builderClasses: List[Class[_ <: ScreenBuilder]] =
      classOf[ActorHolderTest]::
      classOf[TrailedParticleTest] ::
      classOf[ActionPuzzleTableScreen] ::
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
      classOf[ComboEffect] ::
      Nil
  val widgetGroupClasses: List[Class[_ <: WidgetGroup]] =
    classOf[ActionPuzzleTable] :: Nil

  val classBuilders = builderClasses map (c => c.newInstance() -> c.getSimpleName)
  val fileBuilders = files map {
    f => createFromJson(f) -> f
  } collect {
    case (Success(s), f) => s -> f
  }
  val pkgBuilders = screenClasses map {
    clazz => new ScreenBuilder {
      def requiredAssets: Set[(Class[_], Seq[String])] = Set()

      def create(assetManager: AssetManager): Screen = clazz.newInstance()
    } -> clazz.getSimpleName
  }
  val builders: Seq[ScreenBuilder -> String] = classBuilders ++ pkgBuilders ++ fileBuilders
}

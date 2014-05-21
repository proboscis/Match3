package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ScreenBuilder._
import com.glyph._scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.{AssetDescriptor, AssetManager}
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph._scala.lib.libgdx.Builder
import com.glyph._scala.lib.util.Animated
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.game.builders.Builders
import scalaz.Success
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder2
import com.glyph._scala.lib.libgdx.actor.ui.LogViewTest

/**
 * @author glyph
 */
object TestClass {
  type ->[A, B] = (A, B)
  val builderClasses: List[Class[_ <: ScreenBuilder]] =
      classOf[TrailedParticleTest] ::
      classOf[ParticleTest] ::
      classOf[UVTrailTest] ::
      classOf[ImmediateTest] ::
      classOf[WordParticle] :: Nil
  val files = "screens/action.js" :: "screens/puzzle.js" :: Nil
  val screenClasses: List[Class[_ <: Screen]] =
  classOf[SpritePerformanceTest]::
    classOf[GdxParticleTest]::
    classOf[LogViewTest]::
    classOf[ECSTest]::
    classOf[DistanceFieldTest]::
    classOf[MockSelector]::
    classOf[BlurTest]::
    classOf[GameResultMockTest]::
    classOf[GameResultTest] ::
    classOf[PuzzleTest]::
      classOf[ShaderRotationTest] ::
      classOf[ExplosionTest] ::
      classOf[MeshTest] ::
      classOf[TrailTest] ::
      Nil
  val widgetGroupClasses: List[Class[_ <: WidgetGroup]] = Nil
  val animatedClasses: List[Class[_ <: Builder[Actor with Animated]]] = Nil
  val classNameSet = animatedClasses ++ builderClasses ++ screenClasses map (s => s.getSimpleName -> s)
  val classBuilders = builderClasses map (c => c.getSimpleName -> c.newInstance())
  val fileBuilders = files map {
    f => createFromJson(f) -> f
  } collect {
    case (Success(s), f) => f -> s
  }
  val pkgBuilders = screenClasses map {
    clazz => clazz.getSimpleName -> new ScreenBuilder {

      override def requirements: Seq[AssetDescriptor[_]] = Nil

      def create(implicit assetManager: AssetManager): Screen = clazz.newInstance()
    }
  }

  def animatedActorBuilderToScreenBuilder(actorBuilder: Builder[Actor with Animated]): Builder[Screen] =
    Builder(actorBuilder.requirements, assets => new ConfiguredScreen {
      val holder = new AnimatedBuilderHolder2 {}
      root.add(holder).fill.expand
      holder.push(actorBuilder)(assets)
    })

  val builders: Seq[String -> Builder[Screen]] =
    Builders.screenBuilders.toSeq ++
      classBuilders ++
      pkgBuilders ++
      fileBuilders
}

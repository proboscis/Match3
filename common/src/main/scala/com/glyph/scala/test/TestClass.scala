package com.glyph.scala.test

import com.glyph.scala.game.action_puzzle.view.ActionPuzzleTable
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder._
import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.libgdx.Builder
import com.glyph.scala.lib.util.Animated
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.game.builders.Builders
import scalaz.Success
import com.glyph.scala.lib.libgdx.actor.table.{AnimatedBuilderHolder2, AnimatedBuilderHolder}

/**
 * @author glyph
 */
object TestClass {
  type ->[A, B] = (A, B)
  val builderClasses: List[Class[_ <: ScreenBuilder]] =
    classOf[VaryingScreen] ::
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
      Nil
  val widgetGroupClasses: List[Class[_ <: WidgetGroup]] =
    classOf[ActionPuzzleTable] :: Nil
  val animatedClasses: List[Class[_ <: Builder[Actor with Animated]]] =  Nil
  val classNameSet = animatedClasses ++ builderClasses ++ screenClasses map (s => s.getSimpleName -> s)
  val classBuilders = builderClasses map (c => c.getSimpleName -> c.newInstance())
  val fileBuilders = files map {
    f => createFromJson(f) -> f
  } collect {
    case (Success(s), f) => f -> s
  }
  val pkgBuilders = screenClasses map {
    clazz => clazz.getSimpleName -> new ScreenBuilder {
      def requirements: Set[(Class[_], Seq[String])] = Set()

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

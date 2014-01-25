package com.glyph.scala.game.builders

import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.assets.AssetManager
import scala.reflect.ClassTag
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin}
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.action_puzzle.view.Title
import scala.language.implicitConversions

/**
 * @author proboscis
 */
object Builders {
  implicit class ResourceBuilder(name:String){
    def builder[T:ClassTag]:Builder[T] = new Builder[T]{
      def requirements: Builder.Assets = Set(implicitly[ClassTag[T]].runtimeClass->Seq(name))
      def create(implicit assets: AssetManager): T = assets.get[T](name)
    }
  }

  val darkHolo:Builder[Skin] = "skin/holo/Holo-dark-xhdpi.json".builder
  val lightHolo:Builder[Skin] = "skin/holo/Holo-light-xhdpi.json".builder
  val particleTexture:Builder[Texture] = "data/particle.png".builder
  val dummyTexture:Builder[Texture] = "data/dummy.png".builder
  val swordTexture:Builder[Texture] = "data/sword.png".builder
  val roundRectTexture:Builder[Texture]="data/round_rect.png".builder
  val label = (_:Builder[Skin]) map (skin=> new Label(_:String,skin))

  val title = label(lightHolo) map Title.apply
}

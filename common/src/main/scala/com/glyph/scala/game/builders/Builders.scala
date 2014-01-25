package com.glyph.scala.game.builders

import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.assets.AssetManager
import scala.reflect.ClassTag
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin}
import com.badlogic.gdx.graphics.Texture

/**
 * @author proboscis
 */
object Builders {
  implicit def toBuilder[T:ClassTag](resName:String):Builder[T] = new Builder[T]{
    def requirements: Builder.Assets = Set(implicitly[ClassTag[T]].runtimeClass->Seq(resName))

    def create(implicit assets: AssetManager): T = assets.get[T](resName)
  }

  val darkHolo:Builder[Skin] = "skin/holo/Holo-dark-xhdpi.json"
  val lightHolo:Builder[Skin] = "skin/holo/Holo-light-xhdpi.json"
  val particleTexture:Builder[Texture] = "data/particle.png"
  val dummyTexture:Builder[Texture] = "data/dummy.png"
  val label = (_:Builder[Skin]) map (skin=> new Label(_:String,skin))
}

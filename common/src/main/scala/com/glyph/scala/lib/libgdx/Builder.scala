package com.glyph.scala.lib.libgdx

import com.badlogic.gdx.assets.AssetManager

/**
 * thing that require assets to be loaded should use this builder.
 * @tparam T
 */
trait Builder[+T] {
  def requirements: Builder.Assets
  def create(implicit assets:AssetManager): T
}
object Builder{
  type Assets = Set[(Class[_],Seq[String])]
}
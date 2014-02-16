package com.glyph._scala.lib.libgdx

import com.badlogic.gdx.assets.AssetManager
import scalaz.Applicative
import com.glyph._scala.lib.libgdx.Builder.Assets
import com.glyph._scala.lib.libgdx.screen.ScreenBuilder.Assets
import com.glyph._scala.lib.util.extraction.Extractable

/**
 * thing that require assets to be loaded should use this builder.
 * @tparam T
 */
trait Builder[+T] {
  def requirements: Builder.Assets
  def create(implicit assets: AssetManager): T
  def isReady(implicit assets:AssetManager):Boolean = requirements.forall(_._2.forall(assets.isLoaded))
  def map[R](f: T => R): Builder[R] = Builder(requirements,assets =>  f(create(assets)))
  def &[R](tgt:Builder[R]):Builder[(T,R)] = Builder(requirements ++ tgt.requirements,am => create(am)->tgt.create(am))

  /**
   * blocks until all requirements and the other queued resources to be loaded
   * @param assets
   */
  def load(implicit assets:AssetManager){
    for{
      (cls,files)<-requirements
      file <- files
    }{
      assets.load(file,cls)
    }
    assets.finishLoading()
  }
  //flatMap cannot be created
}
trait BuilderOps {
  implicit def applicativeBuilder = new Applicative[Builder] {
    def point[A](a: => A): Builder[A] = Builder(Set(), _ => a)
    def ap[A, B](fa: => Builder[A])(f: => Builder[(A) => B]): Builder[B] = Builder(fa.requirements ++ f.requirements,assets =>  f.create(assets)(fa.create(assets)))
  }
  object &{
    def unapply[A,B](t:(A,B))={
      Some(t)
    }
  }
}
object BuilderOps extends BuilderOps

object Builder {
  type Assets = Set[(Class[_], Seq[String])]

  def apply[T](assets: Assets, constructor: AssetManager => T): Builder[T] = new Builder[T] {
    def requirements: Assets = assets

    def create(implicit assets: AssetManager): T = constructor(assets)
  }
}

object BuilderTest {
  def main(args: Array[String]) {
    import scalaz._
    import Scalaz._
    import BuilderOps._
    val ma = new Builder[Int] {
      def requirements: Assets = Set(classOf[Int] -> Seq("a"))

      def create(implicit assets: AssetManager): Int = 10
    }
    val mb = new Builder[Int] {
      def requirements: Assets = Set(classOf[Int] -> Seq("b"))

      def create(implicit assets: AssetManager): Int = 10
    }
    val b = (ma |@| mb) {
      _ + _
    }
    println(b.requirements)
  }
}


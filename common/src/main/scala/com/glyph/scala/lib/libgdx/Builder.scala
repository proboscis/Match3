package com.glyph.scala.lib.libgdx

import com.badlogic.gdx.assets.AssetManager
import scalaz.{Traverse, Monad, Applicative}
import com.glyph.scala.lib.libgdx.Builder.Assets

/**
 * thing that require assets to be loaded should use this builder.
 * @tparam T
 */
trait Builder[+T]{
  def requirements: Builder.Assets
  def create(implicit assets:AssetManager): T
  def map[R](f:T=>R):Builder[R] = {
    val self = this
    new Builder[R]{
      def requirements: Builder.Assets = self.requirements
      def create(implicit assets: AssetManager): R = f(self.create)
    }
  }
  //flatMap cannot be created
}
object BuilderOps{
  implicit def applicativeBuilder = new Applicative[Builder]{
    def point[A](a: => A): Builder[A] = new Builder[A]{
      def requirements: Assets = Set()
      def create(implicit assets: AssetManager): A = a
    }

    def ap[A, B](fa: => Builder[A])(f: => Builder[(A) => B]): Builder[B] =new Builder[B]{
      def requirements: Assets = fa.requirements ++ f.requirements
      def create(implicit assets: AssetManager): B = f.create(assets)(fa.create(assets))
    }
  }
}
object Builder{
  type Assets = Set[(Class[_],Seq[String])]
}
object BuilderTest{
  def main(args: Array[String]) {
    import scalaz._
    import Scalaz._
    import BuilderOps._
    val ma = new Builder[Int]{
      def requirements: Assets = Set(classOf[Int]->Seq("a"))
      def create(implicit assets: AssetManager): Int = 10
    }
    val mb = new Builder[Int]{
      def requirements: Assets = Set(classOf[Int]->Seq("b"))
      def create(implicit assets: AssetManager): Int = 10
    }
    val b = (ma |@| mb){_+_}
    println(b.requirements)
  }
}


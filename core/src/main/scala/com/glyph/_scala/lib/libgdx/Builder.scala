package com.glyph._scala.lib.libgdx

import com.badlogic.gdx.assets.{AssetLoaderParameters, AssetDescriptor, AssetManager}
import scalaz.Applicative
import com.glyph._scala.lib.util.Logging
import scala.language.implicitConversions
/**
 * thing that require assets to be loaded should use this target.
 * @tparam T
 */
trait Builder[+T] {
  def requirements: Seq[AssetDescriptor[_]]

  def forceCreate(implicit assets: AssetManager): T = {
    load
    create
  }

  def create(implicit assets: AssetManager): T

  def isReady(implicit assets: AssetManager): Boolean = requirements.forall(desc => assets.isLoaded(desc.fileName, desc.`type`))

  def map[R](f: T => R): Builder[R] = Builder(requirements, assets => f(create(assets)))

  def &[R](tgt: Builder[R]): Builder[(T, R)] = Builder(requirements ++ tgt.requirements, am => create(am) -> tgt.create(am))

  /**
   * blocks until all requirements and the other queued resources to be loaded
   * @param assets
   */
  def load(implicit assets: AssetManager) {
    requirements.foreach(assets.load)
    assets.finishLoading()
  }
}

object BuilderOps extends BuilderOps

object Builder extends Logging {

  import BuilderOps._

  type TypedAssets[T] = Seq[(Class[T], Seq[String])]
  type Assets = TypedAssets[_]

  def apply[T: Class](fileName: String, params: AssetLoaderParameters[T]) = new Builder[T] {
    assert(implicitly[Class[T]] != classOf[Object])
    val asset = AssetDescriptor(fileName, params) :: Nil

    override def requirements: Seq[AssetDescriptor[_]] = asset

    override def create(implicit assets: AssetManager): T = assets.get(asset.head)
  }

  def apply[T](asset: AssetDescriptor[T]): Builder[T] = new Builder[T] {
    override def create(implicit assets: AssetManager): T = assets.get[T](asset)

    override def requirements: Seq[AssetDescriptor[_]] = asset :: Nil
  }

  def apply[T](assets: Seq[AssetDescriptor[_]], constructor: AssetManager => T): Builder[T] = new Builder[T] {
    override def requirements: Seq[AssetDescriptor[_]] = assets

    override def create(implicit assets: AssetManager): T = constructor(assets)
  }

  def fromAssets[T](assets: Assets, constructor: AssetManager => T): Builder[T] = new Builder[T] {
    override def requirements: Seq[AssetDescriptor[_]] = assets

    def create(implicit assets: AssetManager): T = constructor(assets)
  }

  def apply[T: Class](fileName: String): Builder[T] = {
    assert(implicitly[Class[T]] != classOf[Object])
    Builder(new AssetDescriptor(fileName, implicitly[Class[T]]) :: Nil, _.get[T](fileName))
  }
}

object AssetDescriptor {
  def apply[T: Class](fileName: String): AssetDescriptor[T] = {
    assert(implicitly[Class[T]] != classOf[Object])
    new AssetDescriptor(fileName, implicitly[Class[T]])
  }

  def apply[T: Class](fileName: String, params: AssetLoaderParameters[T]) = {
    assert(implicitly[Class[T]] != classOf[Object])
    new AssetDescriptor(fileName, implicitly[Class[T]], params)
  }
}

trait BuilderOps {


  implicit def assetIsDescriptors[T](assets: Builder.TypedAssets[T]): Seq[AssetDescriptor[_]] = for {
    (cls, fileNames) <- assets.toSeq
    fileName <- fileNames
  } yield new AssetDescriptor(fileName, cls)

  implicit def applicativeBuilder = new Applicative[Builder] {
    def point[A](a: => A): Builder[A] = Builder(Nil, _ => a)

    def ap[A, B](fa: => Builder[A])(f: => Builder[(A) => B]): Builder[B] = Builder(fa.requirements ++ f.requirements, assets => f.create(assets)(fa.create(assets)))
  }

  object & {
    def unapply[A, B](t: (A, B)) = {
      Some(t)
    }
  }

}

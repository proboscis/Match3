package com.glyph._scala.lib.libgdx.actor.transition

import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import scala.util.Try
import scala.concurrent.Future
import com.glyph._scala.lib.libgdx.Builder
import com.glyph._scala.lib.util.extraction.Extractable
import com.glyph._scala.lib.util.Logging
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedConstructorOps.Extractable2
import com.glyph._scala.lib.util.reactive.Varying
import com.glyph._scala.game.builders.Builders._
import com.glyph._scala.lib.libgdx.actor.{SpriteActor, AnimatedTable}
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.ui.{ScrollPane, Label}
import scala.annotation.target
import scala.annotation

/**
 * @author glyph
 */
trait AnimatedConstructorOps {

  import AnimatedConstructorOps._

  // i want to extract all future, builder, varying, try
  implicit def toACOps[M[_] : Extractable2](self: M[AnimatedConstructor]): ACOps[M] = ACOps(self)

  def extract[F[_] : Extractable2](target: F[AnimatedConstructor]): AnimatedConstructor = ACOps(target).extract
}

object AnimatedConstructorOps extends AnimatedConstructorOps {

  trait Extractable2[E[_]] {
    def extract(target: E[AnimatedConstructor]): AnimatedConstructor
  }

  implicit class ACOps[M[_] : Extractable2](self: M[AnimatedConstructor]) {
    def extract = implicitly[Extractable2[M]].extract(self)
  }
  implicit class ACOps2[M[_]:Functor,T[_]:Extractable2](self:M[T[AnimatedConstructor]]){
    def unwrap = self.map(_.extract)
  }
}

trait Extractors {
  type FF[A] = () => Future[A]
  implicit val extractableBuilder: Extractable[Builder]
  implicit val extractableFF: Extractable[FF]

  implicit def assetManager: AssetManager

  def genExtractable2FF(animation: AnimatedActor): Extractable2[FF] = new Extractable2[FF] {
    override def extract(target: () => Future[AnimatedConstructor]): AnimatedConstructor =
      MonadicAnimated.extract[FF, AnimatedConstructor](target)(animation) |> MonadicAnimated.toAnimatedConstructor
  }

  def genExtractable2Builder(animation: AnimatedActor): Extractable2[Builder] = new Extractable2[Builder] {
    override def extract(target: Builder[AnimatedConstructor]): AnimatedConstructor =
      MonadicAnimated.extract(target)(animation) |> MonadicAnimated.toAnimatedConstructor
  }

  def genExtractable2Varying: Extractable2[Varying] = new Extractable2[Varying] {
    override def extract(target: Varying[AnimatedConstructor]): AnimatedConstructor =
      VaryingAnimatedConstructorHolder(target)
  }

  def genExtractable2Try(handler: Throwable => AnimatedConstructor): Extractable2[Try] = new Extractable2[Try] {
    override def extract(target: Try[AnimatedConstructor]): AnimatedConstructor = target.recover {
      case e => handler(e)
    }.get
  }
}

trait ImplicitExtractors extends Extractors {
  val ffExtraction: AnimatedActor
  val builderExtraction: AnimatedActor
  val errorHandler: Throwable => AnimatedConstructor

  implicit def extractable2FF: Extractable2[FF] = genExtractable2FF(ffExtraction)

  implicit def extractable2Builder: Extractable2[Builder] = genExtractable2Builder(builderExtraction)

  implicit def extractable2Varying: Extractable2[Varying] = genExtractable2Varying

  implicit def extractable2Try: Extractable2[Try] = genExtractable2Try(errorHandler)
}

trait DefaultExtractors extends ImplicitExtractors with Logging {
  lazy val splashAnimation = swordTexture.map {
    tex => new AnimatedTable <| (_.add(new SpriteActor(tex)).fill.expand)
  }.forceCreate
  override val ffExtraction: AnimatedActor = splashAnimation
  override val builderExtraction: AnimatedActor = splashAnimation
  override val errorHandler: (Throwable) => AnimatedConstructor = e => info => callbacks => darkHolo.map {
    skin =>
      errE("handled an error while creating an AnimatedConstructor:")(e)
      val errLabel = new Label(e.getStackTrace.mkString("\n"), skin)
      errLabel.setEllipse(true)
      val pane = new ScrollPane(errLabel,skin)
      pane.setScrollingDisabled(false,false)
      new AnimatedTable <| (
        _.add(pane).fill.expand)
  }.forceCreate
}
package com.glyph._scala.lib.libgdx.actor.transition

import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import scala.util.Try
import scala.concurrent.Future
import com.glyph._scala.lib.libgdx.{GLFuture, Builder}
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
import com.glyph._scala.lib.libgdx.font.FontUtil
import com.glyph._scala.lib.libgdx.skin.FlatSkin
import com.glyph._scala.game.builders.Builders
import com.badlogic.gdx.graphics.g3d.utils.AnimationController

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

}

trait Extractors {
  type FF[A] = () => Future[A]
  implicit val extractableBuilder: Extractable[Builder]
  implicit val extractableFF: Extractable[FF]
  implicit val extractableFuture: Extractable[Future]
  implicit val errorHandlerConstructor:Throwable => AnimatedConstructor
  implicit val errorHandler:Throwable=>AnimatedActor
  implicit def assetManager: AssetManager

  def genExtractable2Future(animation: AnimatedActor): Extractable2[Future] = new Extractable2[Future] {
    override def extract(target: Future[AnimatedConstructor]): AnimatedConstructor =
      MAnimated.extract(target)(animation) |> MAnimated.toAnimatedConstructor
  }

  def genExtractable2FF(animation: AnimatedActor): Extractable2[FF] = new Extractable2[FF] {
    override def extract(target: () => Future[AnimatedConstructor]): AnimatedConstructor =
      MAnimated.extract[FF, AnimatedConstructor](target)(animation) |> MAnimated.toAnimatedConstructor
  }

  def genExtractable2Builder(animation: AnimatedActor): Extractable2[Builder] = new Extractable2[Builder] {
    override def extract(target: Builder[AnimatedConstructor]): AnimatedConstructor =
      MAnimated.extract(target)(animation) |> MAnimated.toAnimatedConstructor
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

/**
 * this is becoming insane...
 */
trait DefaultExtractors extends Extractors with Logging {
  implicit val context = com.glyph._scala.lib.injection.GLExecutionContext.context
  import AnimatedConstructorOps._
  /*
  extractors with specified animation, or error handling method
   */
  implicit def extractable2Future: Extractable2[Future] = genExtractable2Future(futureExtraction)
  implicit def extractable2FF: Extractable2[FF] = genExtractable2FF(ffExtraction)
  implicit def extractable2Builder: Extractable2[Builder] = genExtractable2Builder(builderExtraction)
  implicit def extractable2Varying: Extractable2[Varying] = genExtractable2Varying
  implicit def extractable2Try: Extractable2[Try] = genExtractable2Try(errorHandlerConstructor)
  lazy val debugFont = GLFuture(FontUtil.internalFont("font/corbert.ttf", 140))
  lazy val debugSkin = debugFont.map(font => Builders.dummyTexture.map(tex => FlatSkin.default(font, tex)))
  lazy val splashAnimation = swordTexture.map {
    tex => new AnimatedTable <| (_.add(new SpriteActor(tex)).fill.expand)
  }.forceCreate
  lazy val debugFFExtraction = genExtractable2FF(splashAnimation)
  lazy val debugBuilderExtraction = genExtractable2Builder(splashAnimation)
  lazy val debugFutureExtraction = genExtractable2Future(splashAnimation)
  val stringToExtraction = (str: String) => debugSkin.map(_.map(skin => AnimatedTable(_.fill.expand, new Label(str, skin)))) |> {
    f =>
      MAnimated.extract(f)(splashAnimation).flatMap(b => MAnimated.extract(b)(splashAnimation))
  } |> MAnimated.toAnimatedActor
  val ffExtraction: AnimatedActor = stringToExtraction("Extracting ()=>Future")
  val builderExtraction: AnimatedActor = stringToExtraction("Extracting Builder")
  val futureExtraction:AnimatedActor = stringToExtraction("Extracting Future")
  implicit val errorHandler: (Throwable) => AnimatedActor= e => {
    val abc = debugSkin.map(b => b.map(skin => {
      errE("handled an error while creating an AnimatedConstructor:")(e)
      val errLabel = new Label(e.toString + "\n" + e.getMessage+"\n"+e.getStackTrace.mkString("\n"), skin)
      errLabel.setEllipse(true)
      val pane = new ScrollPane(errLabel)
      pane.setScrollingDisabled(false, false)
       new AnimatedTable <| (
        _.add(pane).fill.expand)
    }))
    MAnimated.extract(abc)(splashAnimation).flatMap(b => MAnimated.extract(b)(splashAnimation)) |> MAnimated.toAnimatedActor
  }
  implicit val errorHandlerConstructor :Throwable=>AnimatedConstructor = errorHandler andThen (a => info=>callbacks=>a)
}
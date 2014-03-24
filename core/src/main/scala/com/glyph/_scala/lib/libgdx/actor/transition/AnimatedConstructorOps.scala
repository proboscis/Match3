package com.glyph._scala.lib.libgdx.actor.transition

import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import scala.util.Try
import scala.concurrent.Future
import com.glyph._scala.lib.libgdx.{GLFuture, Builder}
import com.glyph._scala.lib.util.extraction.Extractable
import com.glyph._scala.lib.util.Logging
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedConstructorOps.{Extractable3, ACG}
import com.glyph._scala.lib.util.reactive.Varying
import com.glyph._scala.game.builders.Builders._
import com.glyph._scala.lib.libgdx.actor.{SpriteActor, AnimatedTable}
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.ui.{ScrollPane, Label}
import com.glyph._scala.lib.libgdx.font.FontUtil
import com.glyph._scala.game.builders.{FlatSkin, Builders}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.updatable.task.ParallelProcessor
import com.glyph._scala.game.Glyphs
import Glyphs._

/**
 * @author glyph
 *         import this and all the Actor/AnimatedActor will be an AnimatedConstructor, and all the
 *         Future/Varying/Try/()=>Future/Builder/ of AnimatedConstructor becomes AnimatedConstructor also.
 */
trait AnimatedConstructorOps {

  import AnimatedConstructorOps._

  implicit def AnimatedActorIsACG[A <: AnimatedActor] = new ACG[A] {
    override def apply(self: A): AnimatedConstructor = info => callbacks => self
  }

  implicit def ActorIsACG[A <: Actor](implicit processor: ParallelProcessor) = new ACG[A] {
    //T is Animation Constructor Generator
    override def apply(self: A): AnimatedConstructor = AnimatedTable(_.fill.expand)(self)
  }

  implicit def AC_IS_ACG[A <: AnimatedConstructor] = new ACG[A] {
    override def apply(self: A): AnimatedConstructor = self
  }

  implicit def extractable3IsACG[E[_] : Extractable3, A: ACG]: ACG[E[A]] = new ACG[E[A]] {
    override def apply(self: E[A]): AnimatedConstructor = implicitly[Extractable3[E]].extract(self)
  }

  //the scala compiler apply function to an instance only once, so i had to make a typeclasses to work around that.
  implicit def ACGIsAC[Self: ACG](self: Self): AnimatedConstructor = implicitly[ACG[Self]].apply(self)
}

object AnimatedConstructorOps extends AnimatedConstructorOps {

  trait ACG[T] {
    //T is Animation Constructor Generator
    def apply(self: T): AnimatedConstructor
  }

  trait Extractable3[E[_]] {
    def extract[A: ACG](target: E[A]): AnimatedConstructor
  }

}

trait Extractors extends Logging {
  type FF[A] = () => Future[A]
  implicit val extractableBuilder: Extractable[Builder]
  implicit val extractableFF: Extractable[FF]
  implicit val extractableFuture: Extractable[Future]
  implicit val errorHandlerConstructor: Throwable => AnimatedConstructor
  implicit val errorHandler: Throwable => AnimatedActor

  implicit def assetManager: AssetManager

  def genE3Future(animation: AnimatedActor): Extractable3[Future] = new Extractable3[Future] {
    override def extract[A: ACG](target: Future[A]): AnimatedConstructor = MAnimated.extract(target)(animation) |> {
      animated => MAnimated.toAC[A](animated, "future")
    }
  }

  def genE3FF(animation: AnimatedActor): Extractable3[FF] = new Extractable3[FF] {
    override def extract[A: ACG](target: () => Future[A]): AnimatedConstructor =
      MAnimated.extract[FF, A](target)(animation) |> {
        animated => MAnimated.toAC[A](animated, "()=>Future")
      }
  }

  def genE3Builder(animation: AnimatedActor): Extractable3[Builder] = new Extractable3[Builder] {
    override def extract[A: ACG](target: Builder[A]): AnimatedConstructor =
      MAnimated.extract(target)(animation) |> {
        animated => MAnimated.toAC[A](animated, "builder")
      }
  }

  def genE3Varying: Extractable3[Varying] = new Extractable3[Varying] {
    override def extract[A: ACG](target: Varying[A]): AnimatedConstructor = VaryingAnimatedConstructorHolder(target.map(implicitly[ACG[A]].apply))
  }

  def genE3Try(handler: Throwable => AnimatedConstructor): Extractable3[Try] = new Extractable3[Try] {
    override def extract[A: ACG](target: Try[A]): AnimatedConstructor = target.map(implicitly[ACG[A]].apply).recover {
      case e => handler(e)
    }.get
  }

  def genE3Option(default: AnimatedConstructor): Extractable3[Option] = new Extractable3[Option] {
    override def extract[A: ACG](target: Option[A]): AnimatedConstructor = {
      err("mapping option to constructor:" + target)
      target.map(implicitly[ACG[A]].apply).getOrElse(default)
    }
  }
}

/**
 * this is becoming insane...
 */
trait DefaultExtractors extends Extractors with Logging {
  implicit val processor: ParallelProcessor
  implicit val context = com.glyph._scala.lib.injection.GLExecutionContext

  import AnimatedConstructorOps._

  implicit def extractable3Future: Extractable3[Future] = genE3Future(futureExtraction)

  implicit def extractable3FF: Extractable3[FF] = genE3FF(ffExtraction)

  implicit def extractable3Builder: Extractable3[Builder] = genE3Builder(builderExtraction)

  implicit def extractable3Varying: Extractable3[Varying] = genE3Varying

  implicit def extractable3Try: Extractable3[Try] = genE3Try(errorHandlerConstructor)

  implicit def extractable3Option = genE3Option(stringToExtraction("None?"))

  lazy val debugFont = GLFuture(FontUtil.internalFont("font/corbert.ttf", 140))

  lazy val debugSkin = debugFont.map(font => Builders.dummyTexture.map(tex => FlatSkin.default(font, tex)))
  lazy val splashAnimation = swordTexture.map {
    tex => new AnimatedTable <| (_.add(new SpriteActor(tex)).fill.expand)
  }.forceCreate
  val stringToExtraction = (str: String) => debugSkin.map(_.map(skin => AnimatedTable(_.fill.expand)(new Label(str, skin)))) |> {
    f =>
      MAnimated.extract(f)(splashAnimation).flatMap(b => MAnimated.extract(b)(splashAnimation))
  } |> MAnimated.toAnimatedActor
  val ffExtraction: AnimatedActor = stringToExtraction("Extracting ()=>Future")
  val builderExtraction: AnimatedActor = stringToExtraction("Extracting Builder")
  val futureExtraction: AnimatedActor = stringToExtraction("Extracting Future")
  implicit val errorHandler: (Throwable) => AnimatedActor = e => {
    val abc = debugSkin.map(b => b.map(skin => {
      errE("handled an error while creating an AnimatedConstructor:")(e)
      val errLabel = new Label(e.toString + "\n" + e.getMessage + "\n" + e.getStackTrace.mkString("\n"), skin)
      errLabel.setEllipse(true)
      val pane = new ScrollPane(errLabel)
      pane.setScrollingDisabled(false, false)
      new AnimatedTable <| (
        _.add(pane).fill.expand)
    }))
    MAnimated.extract(abc)(stringToExtraction("ErrorHandler:Future")).flatMap(b => MAnimated.extract(b)(stringToExtraction("ErrorHandler:Builder"))) |> MAnimated.toAnimatedActor
  }
  implicit val errorHandlerConstructor: Throwable => AnimatedConstructor = e => info => callbacks => errorHandler(e)
}
package com.glyph.scala.lib.libgdx.game

import com.badlogic.gdx.Gdx
import scalaz._
import Scalaz._
import scala.util.Try
import com.glyph.scala.lib.libgdx.screen.{ScreenConfig, ScreenBuilder}
/**
 * @author glyph
 */
class ScreenFileTester(filename:String) extends ScreenBuilderSupport{
  override def create(){
    super.create()
    import ScreenBuilder._

    Try(Gdx.files.internal(filename).readString()) |> tryToVnel2 flatMap createFromJson match{
      case Success(s) => setBuilder(s)
      case Failure(e) => e foreach(_.printStackTrace()); Gdx.app.exit()
    }
  }
  def tryToVnel2[T](t:Try[T]):ValidationNel[Throwable,T] = t match {
    case scala.util.Success(s) => s.successNel
    case scala.util.Failure(f) => f.failNel
  }
}

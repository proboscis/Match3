package com.glyph._scala.lib.libgdx.reactive

import com.glyph._scala.lib.util.reactive.{FileAdapter, RFile}
import java.io.File
import com.badlogic.gdx.{Application, Gdx}
import scalaz._
import Scalaz._
import scala.util.Try

/**
 * @author glyph
 */
class GdxFile(filePath: String) extends RFile(GdxFile.open(filePath))

object GdxFile {
  //TODO fix resource not found errors...
  var absResourceDir: Option[String] = None

  def apply(filePath: String): GdxFile = new GdxFile(filePath)

  val absolute = {
    //new File("").getAbsolutePath.replace(Gdx.files.getExternalStoragePath, "")
    new File("").getAbsolutePath
  }

  def open: (String) => FileAdapter = {
    (path) => new FileAdapter {
      val handle = Gdx.app.getType match {
        case Application.ApplicationType.Android => Gdx.files.internal(path)
        case _ => if (RFile.fileChecker.isDefined) Gdx.files.absolute(dir) else Gdx.files.internal(path)
      }

      //if(debug)Gdx.files.external(dir) else Gdx.files.internal(path)
      def name: String = handle.name()

      def dir = (absResourceDir | absolute) + "/" + path

      println("GdxFilePath:" + dir)

      def lastModified: Long = handle.lastModified()

      def readString = Try(handle.readString)
    }
  }
}


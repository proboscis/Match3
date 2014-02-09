package com.glyph._scala.lib.libgdx.reactive

import com.badlogic.gdx.{Application, Gdx}
import com.glyph._scala.lib.util.reactive.{FileAdapter, RFile}
import java.io.File
import scala.util.Try
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
class GdxFile(filePath: String) extends RFile(GdxFile.open(filePath))

object GdxFile extends Logging{
  //TODO fix resource not found errors...
  var absResourceDir: Option[String] = None


  def apply(filePath: String): RFile = new GdxFile(filePath)

  val absolute = {
    //new File("").getAbsolutePath.replace(Gdx.files.getExternalStoragePath, "")
    new File("").getAbsolutePath
  }

  import Scalaz._
  import scalaz._

  def fileToAdapter(path: String): FileAdapter = new FileAdapter {
    val fileTry = Try(new File(path))

    override def name: String = path

    override def readString: Try[String] = fileTry.flatMap {
      file => Try(io.Source.fromFile(file).mkString)
    }

    override def lastModified: Long = fileTry.map(_.lastModified()) getOrElse 0
  }

  def gdxToAdapter(path: String): FileAdapter = new FileAdapter {
    val handle = Gdx.app.getType match {
      case Application.ApplicationType.Android => Gdx.files.internal(path)
      case _ => if (RFile.fileChecker.isDefined) Gdx.files.absolute(dir)
      else Gdx.files.internal(path)
    }

    //if(debug)Gdx.files.external(dir) else Gdx.files.internal(path)
    def name: String = handle.name()

    def dir = (absResourceDir | absolute) + "/" + path

    println("GdxFilePath:" + dir)

    def lastModified: Long = handle.lastModified()

    def readString = Try(handle.readString)
  }

  def open(path: String): FileAdapter = {
    log("open path:" + path)
    (Option(Gdx.app) as gdxToAdapter(path)) | fileToAdapter("src/main/resources/"+path)
  }
}


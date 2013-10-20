package com.glyph.scala.lib.libgdx.reactive

import com.glyph.scala.lib.util.reactive.{Varying, FileAdapter, RFile}
import java.io.File
import com.badlogic.gdx.{Application, Gdx}

/**
 * @author glyph
 */
class GdxFile(filePath:String) extends RFile(GdxFile.open(filePath))
object GdxFile{
  val debug = false
  def apply(filePath:String):GdxFile = new GdxFile(filePath)
  val absolute = {
    new File("").getAbsolutePath.replace(Gdx.files.getExternalStoragePath, "")
    new File("").getAbsolutePath
  }
  def open: (String) => FileAdapter = {
    (path) => new FileAdapter {
      val handle = Gdx.app.getType match{
        case Application.ApplicationType.Android =>Gdx.files.internal(path)
        case _=>Gdx.files.absolute(dir)
      }//if(debug)Gdx.files.external(dir) else Gdx.files.internal(path)
      def name: String = handle.name()
      def dir = absolute + "/" + path
      println("GdxFilePath:"+dir)
      def lastModified: Long = handle.lastModified()
      import util.control.Exception._
      def readString = allCatch either handle.readString
    }
  }
}


package com.glyph.scala.lib.libgdx.reactive

import com.glyph.scala.lib.util.reactive.{Varying, FileAdapter, RFile}
import java.io.File
import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */
class GdxFile(filePath:String) extends RFile(GdxFile.open(filePath))
object GdxFile{
  val debug = true
  def apply(filePath:String):GdxFile = new GdxFile(filePath)
  val absolute = {
    new File("").getAbsolutePath.replace(Gdx.files.getExternalStoragePath, "")
  }
  val open: (String) => FileAdapter = {
    (path) => new FileAdapter {
      def name: String = handle.name()
      def dir = absolute + "/" + path
      println("GdxFilePath:"+dir)
      val handle = if(debug)Gdx.files.external(dir) else Gdx.files.internal(path)
      def lastModified: Long = handle.lastModified()
      import util.control.Exception._
      def readString = allCatch either handle.readString
    }
  }
}


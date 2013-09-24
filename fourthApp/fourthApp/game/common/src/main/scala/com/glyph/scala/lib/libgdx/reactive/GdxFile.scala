package com.glyph.scala.lib.libgdx.reactive

import com.glyph.scala.lib.util.reactive.{FileAdapter, RFile}
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

      val handle = if(debug)Gdx.files.external(absolute + "/" + path) else Gdx.files.internal(path)
      println(handle.path())
      def lastModified: Long = handle.lastModified()

      def readString(): String = handle.readString()
    }
  }
}

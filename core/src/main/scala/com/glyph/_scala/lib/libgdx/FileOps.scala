package com.glyph._scala.lib.libgdx

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.libgdx.FileOps.FileStringOpsImpl

/**
 * @author glyph
 */
trait FileOps {
  implicit def stringIsFileStringOpsImpl(fileName:String) = new FileStringOpsImpl(fileName)
}
object FileOps extends FileOps{
  implicit class FileStringOpsImpl(val fileName:String) extends AnyVal{
    def internal:FileHandle = Gdx.files.internal(fileName)
    def external:FileHandle = Gdx.files.external(fileName)
  }
}

package com.glyph._scala.lib.libgdx
import scala.language.implicitConversions
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.libgdx.FileOps.{FileHandleOpsImpl, FileStringOpsImpl}
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.lib.libgdx.gl.CanBeTexture
trait CanBeFileHandle[T] extends CanBe[T,FileHandle]
/**
 * @author glyph
 */
trait FileOps {
  implicit def stringIsFileStringOpsImpl(fileName:String) = new FileStringOpsImpl(fileName)
  implicit def fileHandleIsItsOps(handle:FileHandle)= new FileHandleOpsImpl(handle)
  implicit def canBeFileHandleIsFileHandle[T:CanBeFileHandle](self:T)=implicitly[CanBeFileHandle[T]].apply(self)
  implicit object fileHandleCanBeCanBeFileHandle extends CanBeFileHandle[FileHandle]{
    override def apply(self: FileHandle): FileHandle = self
  }
}
object FileOps extends FileOps{
  implicit class FileStringOpsImpl(val fileName:String) extends AnyVal{
    def internal:FileHandle = Gdx.files.internal(fileName)
    def external:FileHandle = Gdx.files.external(fileName)
  }
  implicit class FileHandleOpsImpl(val handle:FileHandle) extends AnyVal{
    def texture:Texture = new Texture(handle)
  }
}

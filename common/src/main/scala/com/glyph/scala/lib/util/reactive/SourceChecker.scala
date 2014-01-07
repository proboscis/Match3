package com.glyph.scala.lib.util.reactive

import java.io.{PrintWriter, File}
import scala.reflect.ClassTag

/**
 * @author glyph
 */
class SourceChecker(sourceDir: String, outputDir: String)
  extends FileChecker
  with Reactor
  with com.glyph.scala.lib.util.Logging {
  val prefix = "$"
  val sourceDirFile = new File(sourceDir)
  val outputDirFile = new File(outputDir)

  def tagToDir(c: ClassTag[_]) = c.runtimeClass.getCanonicalName.replace(".", "/") + ".scala"

  def get[T: ClassTag]: Varying[File] = getFile(sourceDir + "/" + tagToDir(implicitly[ClassTag[T]]))
  def get[T](clsName:String):Varying[File] = getFile(sourceDir+"/"+clsName.replace(".","/")+".scala")
  def onFileChange(file: File): Unit = {
    copyToOutput(file)
  }
  protected def copyToOutput(src: File) {
    val srcParentFile = src.getParentFile.getAbsolutePath.replace(sourceDirFile.getAbsolutePath, "")
    val srcFileDir = src.getAbsolutePath
    val name = src.getName
    log("srcName", srcParentFile, name)
    val changeDir = outputDir + "/" + srcParentFile + "/" + prefix  + name
    val changeFile = new File(changeDir)
    changeFile.getParentFile.mkdirs()
    val out = new PrintWriter(changeDir)
    var replaced = false
    val in = scala.io.Source.fromFile(srcFileDir)
    val lines = in.getLines()
    while (lines.hasNext) {
      val line = lines.next()
      if (!replaced) {
        val regex = """(class|trait|object)\s+(([a-zA-Z\d_$])*)""".r
        out.println(regex.findFirstMatchIn(line) match {
          case Some(m) => {
            replaced = true
            log(line)
            log(m.groupCount)
            for (i <- 0 until m.groupCount) {
              log(i, m.group(i) + "")
            }
            val str = m.group(2)
            if (str != null) line.replace(str, prefix + str) else line
          }
          case None => line
        })
      } else {
        out.println(line)
      }
    }
    in.close()
    out.close()
  }
}

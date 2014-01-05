package com.glyph.scala.lib.util.reactive

import com.glyph.scala.lib.util.Logging
import java.io.File

/**
 * @author glyph
 */
trait FileChecker extends Logging {
  val interval = 1000
  var registeredFiles: File Map (Var[File], Long) = Map()

  def onFileChange(f: File)

  def start() {
    new Thread(new Runnable {
      def run() {
        while (true) {
          //log("check")
          // log(registeredFiles)
          Thread.sleep(interval)
          registeredFiles.synchronized {
            registeredFiles ++= registeredFiles collect {
              case (file, (cb, time)) if file.lastModified() != time => {
                log("file is changed!", file.getAbsolutePath)
                onFileChange(file)
                cb() = file
                file ->(cb, file.lastModified())
              }
            }
          }
        }
      }
    }).start()
  }

  def getFile(fileName: String): Varying[File] = {
    log("getFile", fileName)
    val file = new File(fileName)
    registeredFiles.get(file) match {
      case Some(s) => s._1
      case None =>
        log(file)
        val pair = Var(file) -> file.lastModified()
        registeredFiles.synchronized {
          registeredFiles += file -> pair
        }
        pair._1
    }
    registeredFiles(new File(fileName))._1
  }
}

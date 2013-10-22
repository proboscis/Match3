package com.glyph.scala.lib.util.reactive

import java.io.File
import ref.WeakReference
import io.Source
import scalaz._
import Scalaz._
trait FileAdapter {
  def name: String

  def readString: ValidationNel[Throwable,String]

  def lastModified: Long
}

/**
 * reads file and update if necessary
 * @author glyph
 */
class RFile(val adapter: FileAdapter) extends Varying[ValidationNel[Throwable, String]] {
  //TODO this class may not be releasing the file string after used...
  def this(filePath: String) = {
    this(new FileAdapter {

      import util.control.Exception._

      val file = new File(filePath)

      def name: String = file.getName

      def lastModified = file.lastModified()

      def readString = allCatch.either {
        Source.fromFile(file).getLines().reduceOption {
          _ + "\n" + _
        }.getOrElse("")
      } fold (_.failNel,_.success)
    })
  }

  var _string: ValidationNel[Throwable, String] = null

  def string_=(str: ValidationNel[Throwable, String]) {
    println("load RFile:\n" + adapter.name)
    _string = str
    //println("inDone:"+str)
    notifyObservers(string)
  }

  //this is ok.
  string = adapter.readString

  def string = _string

  def current: ValidationNel[Throwable, String] = _string

  RFile.register(this)

}


object RFile extends Reactor{
  var watchingFiles: List[(WeakReference[RFile], Long)] = Nil
  var fileChecker: Option[Thread] = None
  def enableChecking(interval:Long) {
    fileChecker = (fileChecker | {
      val t = new Thread(new Runnable {
        def run() {
          while (true) {
            Thread.sleep(interval)
            //println("RFile=>file check")
            watchingFiles.synchronized {
              watchingFiles = watchingFiles.collect {
                case (weak@WeakReference(file), lastModified) => {
                  val handle = file.adapter
                  //println(handle.file().lastModified())
                  //println(file,handle.lastModified())
                  if (handle.lastModified != lastModified) {
                    file.string = handle.readString
                  }
                  (weak, handle.lastModified)
                }
              }
            }
          }
        }
      })
      println("FileChecker enabled. polling interval:" + interval)
      t.start();
      t
    }).some
  }

  def register(file: RFile) {
    //TODO register with weak references
    watchingFiles.synchronized {
      watchingFiles = (WeakReference(file), file.adapter.lastModified) :: watchingFiles
    }
  }
}

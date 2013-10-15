package com.glyph.scala.lib.util.reactive

import java.io.File
import ref.WeakReference
import io.Source

trait FileAdapter {
  def name:String
  def readString: Either[Throwable,String]
  def lastModified: Long
}
/**
 * reads file and update if necessary
 * @author glyph
 */
class RFile(val adapter: FileAdapter) extends Varying[Either[Throwable,String]] {

  def this(filePath: String) = {
    this(new FileAdapter {
      import util.control.Exception._
      val file = new File(filePath)
      def name: String = file.getName
      def lastModified = file.lastModified()
      def readString = allCatch either {
        Source.fromFile(file).getLines().reduceOption {
            _ + "\n" + _
          }.getOrElse("")
        }
    })
  }
  var _string: Either[Throwable,String] = null

  def string_=(str: Either[Throwable,String]) {
    if (RFile.DEBUG)println("load RFile:\n"+adapter.name)
    _string = str
    //println("inDone:"+str)
    notifyObservers(string)
  }
  //this is ok.
  string = adapter.readString

  def string = _string

  def current: Either[Throwable,String] = string

  /**
   *
   * @return empty String if failed to load
   */
  def getString:Varying[String] = map{_.fold(err =>{err.printStackTrace();""},identity)}

  RFile.register(this)
}



object RFile {
  val DEBUG = false
  val POLL_INTERVAL = 1000
  var watchingFiles: List[(WeakReference[RFile], Long)] = Nil
  println("FileChecker enabled. polling interval:" + POLL_INTERVAL)
  new Thread(new Runnable {
    def run() {
      while (true) {
        Thread.sleep(POLL_INTERVAL)
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
  }).start()
  def register(file: RFile) {
    //TODO register with weak references
    watchingFiles.synchronized {
      watchingFiles = (WeakReference(file), file.adapter.lastModified) :: watchingFiles
    }
  }
}

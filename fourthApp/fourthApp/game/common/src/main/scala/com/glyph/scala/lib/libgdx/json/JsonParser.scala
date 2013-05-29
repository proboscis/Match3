package com.glyph.scala.lib.libgdx.json

import com.glyph.scala.lib.util.json.ScalaJSON
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.util.callback.Callback

/**
 * @author glyph
 */
class JsonParser(filename:String){
  import com.glyph.scala.lib.util.json.JSON._
  private val reload:Boolean = true
  private var onLoad =(_:ScalaJSON)=>{}
  protected var lastModifiedTime = Gdx.files.internal(filename).lastModified()

  def load(f: ScalaJSON=>Unit){
    onLoad = f
    onLoad(parseJSON(Gdx.files.internal(filename).readString()))
    if (reload) {
      startThread()
    }
  }

  def startThread(){
    new Thread(new Runnable {
      def run() {
        while (true) {
          println("filecheck")
          Thread.sleep(1000)
          val file = Gdx.files.internal(filename)
          val time = file.lastModified()
          if (lastModifiedTime != time) {
            lastModifiedTime = time
            onLoad(parseJSON(file.readString()))
          }
        }
      }
    }).start()
  }
}
object JsonParser{
  def apply(filename:String):JsonParser={
    new JsonParser(filename)
  }
}

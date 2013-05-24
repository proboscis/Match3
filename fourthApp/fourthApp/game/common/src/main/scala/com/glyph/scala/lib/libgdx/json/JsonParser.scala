package com.glyph.scala.lib.libgdx.json

import com.glyph.scala.lib.util.json.ScalaJSON
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.util.callback.Callback

/**
 * @author glyph
 */
abstract class JsonParser(filename:String){
  import com.glyph.scala.lib.util.json.JSON._
  private val reload:Boolean = true
  var onLoad = ()=>{}
  protected var lastModifiedTime = Gdx.files.internal(filename).lastModified()

  def load(f: =>Unit){
    onLoad = ()=>f
    doParse(parseJSON(Gdx.files.internal(filename).readString()))
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
            doParse(parseJSON(file.readString()))
          }
        }
      }
    }).start()
  }
  private def doParse(json:ScalaJSON){
    parse(json)
    onLoad()
  }
  protected def parse(json: ScalaJSON)
}

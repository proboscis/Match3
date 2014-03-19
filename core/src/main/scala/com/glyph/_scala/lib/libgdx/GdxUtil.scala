package com.glyph._scala.lib.libgdx

import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */
object GdxUtil {
  def post(f: =>Unit){
    Gdx.app.postRunnable(new Runnable {
      def run() {
        f
      }
    })
  }
  def postFunction(f:()=>Unit){
    Gdx.app.postRunnable(new Runnable {
      def run() {
        f()
      }
    })
  }
}

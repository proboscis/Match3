package com.glyph.scala.lib.libgdx

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
}

package com.glyph.scala.lib.util

import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */
trait Logging {
  def log(any:Any){
    Gdx.app.log(this.getClass.getSimpleName,any.toString)
  }
  def debug(any:Any,t:Throwable = null){
    if(t != null){
      Gdx.app.debug(this.getClass.getSimpleName,any.toString,t)
    }
    Gdx.app.debug(this.getClass.getSimpleName,any.toString)
  }
  def error(any:Any,t:Throwable = null){
    if(t != null){
      Gdx.app.error(this.getClass.getSimpleName,any.toString,t)
    }
    Gdx.app.error(this.getClass.getSimpleName,any.toString)
  }
}

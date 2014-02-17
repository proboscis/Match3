package com.glyph._scala.social

import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
trait SocialManager {
  def submitScore(score:Long)
  def showGlobalHighScore()
}

object SocialManagerMock extends SocialManager with Logging{

  override def submitScore(score: Long): Unit ={
    log("submit score: "+score)
  }

  override def showGlobalHighScore(): Unit = {
    log("show dash board!")
  }
}
object SocialManager{
  var manager:SocialManager = SocialManagerMock
}
package com.glyph._scala.social

/**
 * @author glyph
 */
trait SocialManager {
  def showDashBoard()
}

object SocialManagerMock extends SocialManager{
  override def showDashBoard(): Unit = {
    println("show dash board!")
  }
}
object SocialManager{
  var manager:SocialManager = SocialManagerMock
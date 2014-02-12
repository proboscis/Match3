package com.glyph._scala

import android.app.Activity
import com.glyph._scala.social.SocialManager
import com.swarmconnect.Swarm
/**
 * @author glyph
 */
class SwarmManager extends SocialManager{
  override def showDashBoard(): Unit = Swarm.showDashboard()
}

object SwarmManager{
  val APP_ID = 9398
  val APP_KEY = "f45a73343aeced592f98ad8b7317d8a9"
  def initialize(activity:Activity){
    Swarm.init(activity,APP_ID,APP_KEY)
  }
}
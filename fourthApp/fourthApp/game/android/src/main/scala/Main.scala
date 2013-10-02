package com.glyph

import android.os.Bundle
import com.badlogic.gdx.backends.android._
import com.glyph.scala.DebugGame

class Main extends AndroidApplication {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = false
    config.useCompass = false
    config.useWakelock = true
    config.useGL20 = true
    new Thread(Thread.currentThread().getThreadGroup,new Runnable {
      def run() {
        initialize(new DebugGame(), config)
      }
    },Thread.currentThread().getName+"logic",64000000).run()
  }
}

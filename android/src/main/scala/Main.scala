package com.glyph

import android.os.Bundle
import com.badlogic.gdx.backends.android._
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.test.TestRunner

class Main extends AndroidApplication with Logging {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = true
    config.useCompass = false
    config.useWakelock = true
    config.useGL20 = true
    new Thread(Thread.currentThread().getThreadGroup, new Runnable {
      def run() {
        initialize(new TestRunner, config)
      }
    }, Thread.currentThread().getName + "logic", 64000000).run()
  }
}

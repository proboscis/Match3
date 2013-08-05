package com.glyph.scala.lib.util

/**
 * @author glyph
 */
package object reactive {
  /**
   * extractor for tuple
   */
  object ~ {
    def unapply[A, B](t: (A, B)) = Some(t)
  }
}

package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.updatable.Updatable

/**
 * @author glyph
 */
trait Task extends Updatable{
  def isCompleted:Boolean
}

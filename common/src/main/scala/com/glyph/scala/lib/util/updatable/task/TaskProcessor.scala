package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.lib.util.updatable.{Updatable}

/**
 * @author glyph
 */
trait TaskProcessor extends Updatable{
  def add(task:Task):TaskProcessor
  def cancel(task:Task)
}

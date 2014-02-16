package com.glyph._scala.lib.util.updatable.task

/**
 * @author glyph
 */
class InterpolatedFunctionTask extends BaseFTask with InterpolationTask{

  override def isCompleted: Boolean = super[BaseFTask].isCompleted || super[InterpolationTask].isCompleted

  def apply(alpha: Float){
    updater(alpha)
  }
}

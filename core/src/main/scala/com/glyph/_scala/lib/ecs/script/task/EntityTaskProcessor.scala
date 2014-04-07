package com.glyph._scala.lib.ecs.script.task

import com.glyph._scala.lib.util.updatable.task.{Task, ParallelProcessor}
import com.glyph._scala.lib.ecs.script.Script

/**
 * @author glyph
 */
class EntityTaskProcessor extends ParallelProcessor with Script{
  override def update(delta: Float): Unit = {
    super[Script].update(delta)
    super[ParallelProcessor].update(delta)
  }

  override def reset(): Unit = {
    super.reset()
    clearTasks()
  }
}

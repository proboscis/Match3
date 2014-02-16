package com.glyph._scala.lib.libgdx.actor.ui

import com.glyph._scala.lib.util.reactive.{Reactor, Reactive}
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
trait ReactiveActor[T] extends Actor with Reactor{
  def reactiveValue: Reactive[T]
}

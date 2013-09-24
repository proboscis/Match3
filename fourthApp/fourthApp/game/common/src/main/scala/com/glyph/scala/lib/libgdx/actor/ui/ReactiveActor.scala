package com.glyph.scala.lib.libgdx.actor.ui

import com.glyph.scala.lib.util.reactive.{Reactor, Reactive}
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
trait ReactiveActor extends Actor with Reactor{
  def reactiveValue: Reactive[_]
}

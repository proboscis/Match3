package com.glyph._scala.lib.libgdx.actor.ui

import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Label}
import com.glyph._scala.lib.util.reactive.{Reactive, Reactor, Varying}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

/**
 * @author glyph
 */
class RLabel(skin:Skin,text:Varying[String]) extends Label(text(),skin) with ReactiveActor[String]{
  reactVar(text)(setText)
  def reactiveValue: Reactive[String] = text
}

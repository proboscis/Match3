package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.game.puzzle.model.Game
import com.glyph.scala.lib.libgdx.actor.ui.RLabel

/**
 * @author glyph
 */
class HeaderView(game:Game) extends Table {
  add(new RLabel(skin, game.player.position->{_+"/"+game.dungeon.goal})).center
}

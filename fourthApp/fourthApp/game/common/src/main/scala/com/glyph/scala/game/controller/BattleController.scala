package com.glyph.scala.game.controller

import com.glyph.scala.lib.util.drawable.DecalRenderer
import com.glyph.scala.game.model.Battle
import com.glyph.scala.lib.util.updatable.Updatables
import com.glyph.scala.game.view.CharacterRenderer
import com.glyph.scala.lib.util.updatable.task.Sequence
import com.glyph.scala.lib.util.updatable.decal.DecalTask.MoveTo
import com.badlogic.gdx.math.{Vector3, Interpolation}
import com.glyph.scala.lib.graphics.util.decal.Decal

/**
 * @author glyph
 */
class BattleController(renderer:DecalRenderer,battle:Battle) extends Updatables{
  val PLAYER_POSITION = new Vector3(2,6f,0)
  //TODO プレイヤーや敵の座標の決定
  //TODO 戦闘システムの実装
  val player = new CharacterRenderer
  val sequence = new Sequence{}
  player.decal.setPosition(PLAYER_POSITION)
  sequence.addTask(new MoveTo {
    val decal: Decal = player.decal
    val duration: Float = 1f
    interpolation = Interpolation.bounceOut
    end.set(decal.getPosition).sub(0,5.5f,0)
  })
  this.add(sequence)
  renderer.add(player)
}

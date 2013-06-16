package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui.{WidgetGroup, Table}
import com.glyph.scala.game.puzzle.model.Puzzle
import com.glyph.scala.lib.libgdx.actor.Scissor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d

/**
 * @author glyph
 */
class PuzzleView(puzzle: Puzzle) extends Table with Scissor {
  def marginX = getWidth/(5f + 6f*0.1f)*0.1f
  def marginY = getHeight/(5f + 6f*0.1f)*0.1f
  def panelW = getWidth/(5f + 6f*0.1f)
  def panelH = getHeight/(5f + 6f*0.1f)
  def divX = puzzleGroup.getWidth/5f
  def divY = puzzleGroup.getHeight/5f
  val puzzleGroup = new Group with Scissor
  this.add(new Group{
    this.addActor(puzzleGroup)
    override def setWidth(width: Float) {
      super.setWidth(width)
      puzzleGroup.setWidth(this.getWidth -marginX)
      setupPosition()
    }

    override def setHeight(height: Float) {
      super.setHeight(height)
      puzzleGroup.setHeight(this.getHeight-marginY)
      setupPosition()
    }
    def setupPosition(){
      setPosition(marginX/2f,marginY/2f)
    }
  }).fill().expand()
  this.debug()
}

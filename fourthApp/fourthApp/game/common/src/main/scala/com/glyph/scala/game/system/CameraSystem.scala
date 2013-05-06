package com.glyph.scala.game.system

import com.glyph.scala.game.GameContext
import com.badlogic.gdx.graphics.Camera
import com.glyph.scala.Glyph
import com.glyph.scala.lib.util.Disposable
import com.glyph.scala.lib.math.Vec2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * @author glyph
 */
class CameraSystem(context:GameContext,group:Group)extends Disposable{
  Glyph.deprecatedLog("CameraSystem","construct")
  var tmp = new Vec2
  var target:Vec2 = null
  var pos:Vec2 = new Vec2(group.getX,group.getY)
  def setTarget(tgt:Vec2){
    target = tgt
  }
  def onRenderFrame(){
    if(target != null){
      tmp.set(-target.x,-target.y).add(group.getWidth/2,group.getHeight/2)
      pos.set(group.getX,group.getY).lerp(tmp,0.1f)
      group.setPosition(pos.x,pos.y)
    }
  }
  def dispose() {
    target = null
  }
}

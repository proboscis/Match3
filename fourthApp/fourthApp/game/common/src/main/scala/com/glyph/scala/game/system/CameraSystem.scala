package com.glyph.scala.game.system

import com.glyph.scala.game.GameContext
import com.badlogic.gdx.graphics.Camera
import com.glyph.scala.Glyph
import com.glyph.scala.lib.util.Disposable
import com.glyph.scala.lib.math.Vec2
import com.badlogic.gdx.math.Vector3

/**
 * @author glyph
 */
class CameraSystem(context:GameContext,camera:Camera)extends Disposable{
  Glyph.log("CameraSystem","construct")
  var target:Vec2 = null
  var targetTmp = new Vector3()
  def setTarget(tgt:Vec2){
    target = tgt
  }
  def onRenderFrame(){
    if(target != null){
      camera.position.set(camera.position.lerp(targetTmp.set(target.x,target.y,0),0.1f))
    }
  }
  def dispose() {
    target = null
    targetTmp = null
  }
}

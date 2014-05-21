package com.glyph._scala.lib.ecs.script

import com.glyph._scala.lib.util.pool.Poolable
import com.glyph._scala.lib.ecs.Entity

/**
 * @author glyph
 */
trait Script extends Poolable{
  var entity:Entity = null
  private var _initialized = false
  def isInitialized = _initialized
  def initialize(self:Entity){
    entity = self
    _initialized = true
  }
  def update(delta:Float){
    //log("update")
  }
  def reset(){
    entity = null
    _initialized = false
  }
  def remove(){
    entity -= this
  }
}

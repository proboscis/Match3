package com.glyph.scala.lib.graphics.util.decal

import com.badlogic.gdx.graphics.g3d.decals.{DecalMaterial, Decal=>GdxDecal}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.math.Vector3

/**
 * @author glyph
 */
class Decal(width:Float,height:Float,textureRegion:TextureRegion ,srcBlendFactor :Int, dstBlendFactor:Int) extends GdxDecal{
  setTextureRegion(textureRegion)
  setBlending(srcBlendFactor,dstBlendFactor)
  dimensions.x = width
  dimensions.y = height
  setColor(1,1,1,1)
  def this(width:Float,height:Float,textureRegion:TextureRegion,hasTransparency:Boolean){
    this(width, height, textureRegion, if(hasTransparency) GL10.GL_SRC_ALPHA else DecalMaterial.NO_BLEND,
      if (hasTransparency)  GL10.GL_ONE_MINUS_SRC_ALPHA else DecalMaterial.NO_BLEND)
  }
  def this(width:Float,height:Float,textureRegion:TextureRegion){
    this(width, height, textureRegion, DecalMaterial.NO_BLEND, DecalMaterial.NO_BLEND)
  }
  def this(textureRegion:TextureRegion,hasTransparency:Boolean){
    this(textureRegion.getRegionWidth, textureRegion.getRegionHeight, textureRegion,
      if(hasTransparency) GL10.GL_SRC_ALPHA else DecalMaterial.NO_BLEND, if(hasTransparency) GL10.GL_ONE_MINUS_SRC_ALPHA
        else DecalMaterial.NO_BLEND)
  }
  def this(textureRegion:TextureRegion){
    this(textureRegion.getRegionWidth, textureRegion.getRegionHeight, textureRegion, DecalMaterial.NO_BLEND,
      DecalMaterial.NO_BLEND)
  }

  def setPosition(pos:Vector3){
    setPosition(pos.x,pos.y,pos.z)
  }
}

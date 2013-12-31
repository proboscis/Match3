package com.glyph.scala.lib.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils

/**
 * @author glyph
 */
object ColorUtil extends Logging{
  
  def ColorToHSV(c:Color,dst:HSV){
    import Math._
    val r = c.r
    val g = c.g
    val b = c.b
    val MAX = max(r,max(g,b))
    val MIN = min(r,min(g,b))
    val mult = 60f/(MAX-MIN)
    val h:Float = if(r >= g && r >= b){
      mult * (g-b)
    }else if(g >= r && g >= b){
      mult *(b-r) + 120f
    }else if(b >= r && b >= g){
      mult * (r-g) + 240f
    }else throw new RuntimeException("something went wrong with ColorToHSV method")
    val s = (MAX-MIN)/MAX
    val v = MAX
    dst.h = if(h<0)h+360f else h
    dst.s = s
    dst.v = v
  }
  //TODO this is called
  def ColorToHSV(c:Color):HSV ={
    import Math._
    val r = c.r
    val g = c.g
    val b = c.b
    val MAX = max(r,max(g,b))
    val MIN = min(r,min(g,b))
    val mult = 60f/(MAX-MIN)
    val h:Float = if(r >= g && r >= b){
      mult * (g-b)
    }else if(g >= r && g >= b){
      mult *(b-r) + 120f
    }else if(b >= r && b >= g){
      mult * (r-g) + 240f
    }else throw new RuntimeException("something went wrong with ColorToHSV method")
    val s = (MAX-MIN)/MAX
    val v = MAX
    HSV(if(h<0)h+360f else h,s,v)
  }
  case class HSV(var h:Float,var s:Float,var v:Float){
    def toColor:Color = if(s != 0 && !h.isNaN){
      s = Math.min(s,1f)
      v = Math.min(v,1f)
      val hi = h.toInt/60%6
      val f = h/60f-hi
      val p = v * (1-s)
      val q = v * ( 1-f*s)
      val t = v * (1 - (1 - f)*s)
      hi match{
        case 0 => new Color(v,t,p,1)
        case 1 => new Color(q,v,p,1)
        case 2 => new Color(p,v,t,1)
        case 3 => new Color(p,q,v,1)
        case 4 => new Color(t,p,v,1)
        case 5 => new Color(v,p,q,1)
      }
    } else {
      new Color(v,v,v,1)
    }
    def toColor(dst:Color):Color = if(s != 0 && !h.isNaN){
      s = Math.min(s,1f)
      v = Math.min(v,1f)
      val hi = h.toInt/60%6
      val f = h/60f-hi
      val p = v * (1-s)
      val q = v * ( 1-f*s)
      val t = v * (1 - (1 - f)*s)
      hi match{
        case 0 => dst.set(v,t,p,1)
        case 1 => dst.set(q,v,p,1)
        case 2 => dst.set(p,v,t,1)
        case 3 => dst.set(p,q,v,1)
        case 4 => dst.set(t,p,v,1)
        case 5 => dst.set(v,p,q,1)
      }
    } else {
      dst.set(v,v,v,1)
    }
    def add(h:Float,s:Float,v:Float):HSV={
      this.h += h
      this.s += s
      this.v += v
      this
    }
  }

}

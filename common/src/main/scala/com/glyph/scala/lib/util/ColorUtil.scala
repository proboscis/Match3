package com.glyph.scala.lib.util

/**
 * @author glyph
 */
object ColorUtil {
  def RGBtoHSV(red:Int,green:Int,blue:Int):Array[Int] ={
    val hsv = new Array[Int](3)
    val max = Math.max(red, Math.max(green, blue))
    val min = Math.min(red, Math.min(green, blue))

    // h
    if(max == min){
      hsv(0) = 0
    }
    else if(max == red){
      hsv(0) = (60 * (green - blue) / (max - min) + 360) % 360
    }
    else if(max == green){
      hsv(0) = (60 * (blue - red) / (max - min)) + 120
    }
    else if(max == blue){
      hsv(0) = (60 * (red - green) / (max - min)) + 240
    }

    // s
    if(max == 0){
      hsv(1) = 0
    }
    else{
      hsv(1) = 255 * ((max - min) / max)
    }

    // v
    hsv(2) = max

    hsv
  }
  def HSVtoRGB(h:Int,s:Int,v:Int):Array[Int] ={
    var f:Float = 0f
    var i, p, q, t : Int = 0
    val rgb = new Array[Int](3)

    i = (Math.floor(h / 60.0f) % 6).toInt
    f = (h / 60.0f) - Math.floor(h / 60.0f).toFloat
    p = Math.round(v * (1.0f - (s / 255.0f)))
    q = Math.round(v * (1.0f - (s / 255.0f) * f))
    t = Math.round(v * (1.0f - (s / 255.0f) * (1.0f - f)))

    i match{
      case 0 => rgb(0) = v; rgb(1) = t; rgb(2) = p
      case 1 => rgb(0) = q; rgb(1) = v; rgb(2) = p
      case 2 => rgb(0) = p; rgb(1) = v; rgb(2) = t
      case 3 => rgb(0) = p; rgb(1) = q; rgb(2) = v
      case 4 => rgb(0) = t; rgb(1) = p; rgb(2) = v
      case 5 => rgb(0) = v; rgb(1) = p; rgb(2) = q
    }
    rgb
  }
}

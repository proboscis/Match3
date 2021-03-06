package com.glyph._scala.lib.util
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.glyph._scala.game.action_puzzle.ColorTheme
import java.io.{File, PrintWriter}

/**
 * @author proboscis
 */

import scalaz._
import Scalaz._
import MathUtils._
object GimpPalette {

  def toGimpPalette(colors: Seq[Color], name: String): String =
    colors.map(c => (c, "null")).toSet |> toGimpPalette apply name
  def toGimpPalette(colors: Set[(Color, String)])(name: String): String ={
    s"""GIMP Palette
      |Name:$name
    """.stripMargin + "\n" +
    colorLines(colors)
  }
  def colorLines(colors:Set[(Color,String)]):String = colors map {
    case (color, n) => colorToLine(color) + " " + n
  } mkString "\n"
  def fColorToIColor(f: Float): Int = clamp((f * 255).toInt, 0, 255)
  def colorToSeq(color: Color): Seq[Float] =
    (color.r :: color.g :: color.b  :: Nil) map (_ * color.a)

  def colorToLine(color: Color): String =
    color |> colorToSeq map fColorToIColor mkString " "
  def main(args: Array[String]) {
    import Function.tupled
    val str = ColorTheme.varyingColorMap().map(tupled((a, b) => (b, a))).toSet |> toGimpPalette apply "FlatDesign"
    println(str)
    new PrintWriter(new File("./flat_palette.txt")).<|(_.println(str)).close()
    println("written a file")
  }
}

object JsonPalette{
  import Function.tupled
  def colorToLine(c:Color,name:String):String = s"$name:{a:${c.a},b:${c.b},g:${c.g},r:${c.r}}"
  def colorsToLines(colors:Set[(Color,String)]):String = colors.map(tupled(colorToLine)).mkString(",\n")

  def main(args: Array[String]) {
    val json = ColorTheme.varyingColorMap().map(tupled((a, b) => (b, a))).toSet |> colorsToLines
    println(json)
  }
}
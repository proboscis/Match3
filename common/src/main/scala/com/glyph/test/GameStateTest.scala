package com.glyph.test

import scala.annotation.tailrec

/**
 * @author glyph
 */
object GameStateTest {
  var input = (1 to 20000 map {
    _.toChar
  }).toList ::: 'g' :: Nil

  def main(args: Array[String]) {
    start {
      case "finish" => finish {
        case _ => //do nothing
      }
      case _ => println("???")
    }
  }

  type State = (Any => Unit) => Unit

  def start: State = func => {
    def loop: State = f => {
      process {
        case 'g' => f("finish")
        case _ => loop(f)
      }
    }
    loop {
      case "finish" => func("finish")
    }
  }

  def process: State = func => {
    val (head :: tail) = input
    input = tail
    println(head)
    func(head)
  }

  def finish: State = func => {
    println("finished!")
  }
}

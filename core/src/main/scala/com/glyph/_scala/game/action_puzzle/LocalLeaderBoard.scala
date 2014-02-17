package com.glyph._scala.game.action_puzzle

import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.util.Logging
import scala.util.Try

/**
 * @author glyph
 */
case class LocalLeaderBoard(name: String) {
  Gdx.files.internal(name)
}

object LocalLeaderBoard extends Logging {

  import spray.json._
  import DefaultJsonProtocol._
  import LeaderBoard._
  import spray.json._
  import scalaz._
  import Scalaz._

  def load(name: String): Scores =  Try(Gdx.files.local(name).readString().asJson.convertTo[Seq[(Long,Long)]]).getOrElse(Seq())

  def save(name: String, scores: Scores): Unit = {
    scores.take(100).toJson.prettyPrint <|
      log <|
      (Gdx.files.local(name).writeString(_: String, false))
  }
}

object LeaderBoard {
  type Score = Long
  type TimeStamp = Long
  type Entry = (TimeStamp, Score)
  type Scores = Seq[Entry]
}

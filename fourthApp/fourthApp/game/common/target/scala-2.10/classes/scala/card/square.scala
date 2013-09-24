import com.glyph.scala.game.puzzle.controller.PuzzleGameController

(c:PuzzleGameController)=>{
  c.destroy((for(i <- 1 to 4; j <- 1 to 4) yield (i,j)):_*)
}

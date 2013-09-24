import com.glyph.scala.game.puzzle.controller.PuzzleGameController

(controller: PuzzleGameController) => {
  controller.destroy(1 to 6 map {
    i => (i - 1, 6 - i)
  }: _*)
}
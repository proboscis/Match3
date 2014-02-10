package com.glyph._scala.test

import com.glyph._scala.game.builders.Builders
import com.glyph._scala.lib.libgdx.{GLFuture, BuilderExtractor2, Builder}
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.libgdx.actor.transition.{LoadingAnimation, StackedAnimatedActorHolder, AnimatedExtractor, AnimatedManager}
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor
import com.glyph._scala.game.action_puzzle.view.animated.Menu
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.libgdx.actor.{AnimatedTable, Tasking}
import com.glyph._scala.lib.util.extraction.{ExtractableFunctionFuture, Extractable}
import com.badlogic.gdx.assets.AssetManager
import scala.concurrent.Future
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin}
import com.glyph._scala.game.action_puzzle.{ColorTheme, ComboPuzzle}
import com.glyph._scala.lib.libgdx.skin.FlatSkin
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph._scala.game.Glyphs
import com.glyph._scala.game.action_puzzle.view.ActionPuzzleTable

object AnimatedHolder2Test {
  //TODO make unit test for animated classes

  import scalaz._
  import Scalaz._
  import Glyphs._
  def extract[E[_], T](builder: E[T])(mapper: T => AnimatedConstructor)(name: String)(implicit extractor: Extractable[E], assets: AssetManager): AnimatedConstructor =
    info => callbacks => new AnimatedExtractor(info, callbacks, builder, mapper) with LoadingAnimation[E, T] {
      override val loadingAnimation: AnimatedActor = new AnimatedTable {
        debug()
        val actor = new Label(name, Builders.darkHolo.create(assets))
        add(actor).fill.expand
      }
    }

  val builder = Builder(Set(classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")), assets => new ConfiguredScreen {
    val holder = new StackedAnimatedActorHolder with Tasking {} <| (root.add(_).fill.expand)
    implicit val _1 = assets
    implicit val _2 = holder
    implicit val builderExtractor = new BuilderExtractor2
    implicit val functionExtractor = ExtractableFunctionFuture
    val flat = Builders.dummyTexture.map(tex =>
      new FlatSkin(
        ColorTheme.varyingColorMap(),
        c =>new SpriteDrawable(new Sprite(tex) <| (_.setColor(c))),
        "skin/holo/Holo-dark-xhdpi.json".fromAssets[Skin].getFont("default-font")
      ))
    val title = extract(Builders.title)(a => a)("loading")
    val menu = extract(flat map Menu.constructor)(a => a)("loading")
    import Builders._
    val puzzleBuilder = (roundRectTexture |@| particleTexture |@| dummyTexture |@| flat)(
      (a,b,c,d)=>()=>{
        GLFuture(ActionPuzzleTable.animated(new ComboPuzzle)(a,b,c,d))
      })
    //Builders.actionPuzzleFunctionBuilder(() => new ComboPuzzle)


    //you need to specify the type lambda since the compiler cannot infer the nested higher kinded types.
    val puzzle = extract(puzzleBuilder)(builder => extract[({type l[A] = () => Future[A]})#l, AnimatedConstructor](builder)(a => a)("initializing"))("loading")
    val push = holder.push _
    val manager = new AnimatedManager(
      Map(
        title -> Map("dummy" ->(push, menu)),
        menu -> Map(
          "Title" ->(push, title),
          "Menu" ->(push, menu),
          "Puzzle" ->(push, puzzle)
        ),
        puzzle -> Map("game_over"->(push,title))
      )
    )
    manager.start(title, Map(), push)
  })
}

var Card = com.glyph.scala.game.puzzle.model.cards.Card;
var Function = Packages.scala.Function0;
var Tuple = Packages.scala.Tuple2
var out = java.lang.System.out
new Function{
    apply:function(){
        return new Card{
            apply:function(c){
                out.println("apply card")
                var array = new Array()
                for(i = 1;i < 5; i++){
                    array.push(new Tuple(i,i))
                    array.push(new Tuple(5-i,i))
                }
                c.destroy(array)
            },
            toString:function(){
                return "Meteor"
            }
        }
    }
}

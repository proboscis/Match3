var Table = Packages.com.badlogic.gdx.scenes.scene2d.ui.Table
var width =VIRTUAL_WIDTH
var height = VIRTUAL_HEIGHT
var Color = Packages.com.badlogic.gdx.graphics.Color
/*
function(){
    var out = java.lang.System.out
    self.clear()
    out.println("setup PuzzleGameView")
    puzzleGroup.addActor(puzzleView)
    puzzleGroup.addActor(slideView)
    table.top()
    table.add().expandX().height(VIRTUAL_WIDTH / 9 * 1.4).row() //dummy for ads
    //table.add(headerView).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH / 9).top().row()
    table.add(puzzleGroup).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH).top().row()
    table.add(headerView).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH / 9).top().row()
    table.add(statusView).expandX().height(VIRTUAL_WIDTH / 9 * 0.7).fill().row()
    table.add(cardView).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH / 5 * 1.618)
    table.row()
    table.debug()
    root.addActor(table)
    table.layout() //somehow this is required
    self.add(root).fill().expand()
    self.layout()
    out.println(VIRTUAL_WIDTH)
}*/
function(){
    var out = java.lang.System.out
    self.clear()
    out.println("gameView.js: setup PuzzleGameView")
    puzzleGroup.addActor(puzzleView)
    puzzleGroup.addActor(slideView)
    table.top()
    table.add().expandX().height(VIRTUAL_WIDTH / 9 * 1.4).row() //dummy for ads
    table.add(puzzleGroup).size(VIRTUAL_WIDTH, VIRTUAL_WIDTH).top().row()
    //thunderGauge.setColor(Color.YELLOW)
    //waterGauge.setColor(Color.BLUE)
    //fireGauge.setColor(Color.RED)
    //lifeGauge.setColor(Color.RED)
    var group = new Table()
    group.debug()
    group.add(thunderGauge).expand(1,1).fill(0.6,0.8)
    group.add(waterGauge).expand(1,1).fill(0.6,0.8)
    group.add(fireGauge).expand(1,1).fill(0.6,0.8)
    group.add(headerView).expand(6,1).fill(1,0.1)
    group.add(lifeGauge).expand(3,1).fill(0.8,0.8)
    table.add(group).size(width,width*0.2).row()
    table.add(cardView).size(width, width*0.3)
    table.row()
    table.debug()
    root.addActor(table)
    table.layout() //somehow this is required
    self.add(root).fill().expand()
    self.layout()
    out.println("gameView.js width:"+VIRTUAL_WIDTH)
}
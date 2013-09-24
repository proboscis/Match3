var Gauge = Packages.com.glyph.scala.lib.libgdx.actor.ui.Gauge;
var RLabel = Packages.com.glyph.scala.lib.libgdx.actor.ui.RLabel;
var Table = Packages.com.badlogic.gdx.scenes.scene2d.ui.Table
table.clear();
layers.addActor(new Gauge(gaugeAlpha))
var lifeLabel = new RLabel(skin,lifeText)
lifeLabel.setFontScale(0.8)
wrapper.add(lifeLabel).center()
layers.addActor(wrapper)
var dadLabel = new RLabel(skin,dadText)
var actionLabel = new RLabel(skin,actionText)
//var deckLabel = new RLabel(skin,deckText)
//var discardLabel = new RLabel(skin,discardText)
dadLabel.setFontScale(0.5)
//discardLabel.setFontScale(0.4)
table.add(dadLabel).expand(1,1).fill(0,0.8)
table.add(layers).expand(8,1).fill(1,1)
table.add(actionLabel).expand(1,1).fill(0,0.8)
//table.add(discardLabel).expand(1,1)
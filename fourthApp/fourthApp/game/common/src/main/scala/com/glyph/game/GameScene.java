package com.glyph.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.glyph.libgdx.Engine;
import com.glyph.libgdx.Scene;
import com.glyph.libgdx.asset.AM;
import com.glyph.libgdx.surface.Surface;
public class GameScene extends Scene {
    protected FPSLogger mFpsLogger;
    protected Table mGameTable;
    protected Surface mGameSurface;
    protected Stage mGameStage;
    protected Stage mUIStage;

    public GameScene(int width, int height) {
        super(width, height);
        initializeView();
    }

    private void initializeView() {
        mGameSurface = new Surface(Engine.VIRTUAL_WIDTH,
                Engine.VIRTUAL_HEIGHT / 2);
        mGameStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT,
                true);
        mUIStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT, true);
        this.addProcessor(mGameStage);
        this.addProcessor(mUIStage);
        this.addStage(mGameStage);
        this.addStage(mUIStage);

        mGameTable = new Table();

        Skin skin = new Skin();
        Texture ra = AM.instance().get("data/rightArrow.png");
        Texture la = AM.instance().get("data/leftArrow.png");
        Texture exe = AM.instance().get("data/lightbulb32.png");
        skin.add("right", ra);
        skin.add("left", la);
        skin.add("exec", exe);
        ButtonStyle bstyle = new ButtonStyle();
        bstyle.up = skin.getDrawable("right");
        skin.add("default", bstyle);
        // bstyle.up =

        final Button right = new Button(skin.getDrawable("right"));

        final Button left = new Button(skin.getDrawable("left"));

        final Button exec = new Button(skin.getDrawable("exec"));

        Table t = mGameTable;
        t.setSize(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT);
        t.row();
        t.add(mGameSurface).expandX().height(Engine.VIRTUAL_HEIGHT / 2).fill()
                .colspan(3);
        t.row();
        t.add().colspan(3).expand(1, 3);
        t.row();
        t.add(left).expand(1, 1);
        t.add(exec).expand(1, 1);
        t.add(right).expand(1, 1);
        t.debug();
        mUIStage.addActor(t);
        mGameTable.layout();
        //
        // /**
        // * SurfaceSprite test
        // */
        // Texture skeleton = AM.instance().get("data/skeleton.png");
        // final SurfaceSprite sprite = new SurfaceSprite(skeleton);
        // sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
        // mGameSurface.add(sprite);
        mFpsLogger = new FPSLogger();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScene", "resize");
        super.resize(width, height);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mFpsLogger.log();

        Table.drawDebug(mGameStage);
        Table.drawDebug(mUIStage);
        mGameSurface.resize();
    }
}

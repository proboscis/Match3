package com.glyph.libgdx.surface.drawable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SurfaceSprite extends Sprite implements SurfaceDrawable {
    public SurfaceSprite(TextureRegion region) {
        super(region);
    }

    public SurfaceSprite(Texture tex) {
        super(tex);
    }
}

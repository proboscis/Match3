package com.glyph.libgdx.surface.drawable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface SurfaceDrawable {
    public float zOrder();
    public void draw(SpriteBatch batch, float parentAlpha);
}

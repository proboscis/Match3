package com.glyph.libgdx.particle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Particle {
    public void update(float delta);

    public abstract void draw(SpriteBatch batch);
}

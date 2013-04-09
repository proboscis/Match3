package com.glyph.libgdx.particle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * A class that represents a particle consisting of Sprite class
 *
 * @author glyph
 */
public class SpriteParticle extends Sprite implements Poolable, Particle {
    //private static int sCount = 0;
    Vector2 mVelocity;
    private static Vector2 tmp = new Vector2();

    public SpriteParticle() {
        //sCount++;
        //Gdx.app.log("SpriteParticle",""+sCount);
        mVelocity = new Vector2();
    }

    @Override
    public void reset() {
        setX(0);
        setY(0);
        setColor(1, 1, 1, 1);
        setSize(0, 0);
        setScale(1);
        setOrigin(0, 0);
        setRotation(0);
        setRegion(0, 0, 0, 0);
        mVelocity.set(0, 0);
    }

    /**
     * value of region will be copied.
     *
     * @param region
     */
    public void init(TextureRegion region) {
        setRegion(region);
        setSize(region.getRegionWidth(), region.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void update(float delta) {
        tmp.set(mVelocity);
        tmp.mul(delta);
        setPosition(getX() + tmp.x, getY() + tmp.y);
        tmp.set(0, 0);
    }

    public void setVelocity(Vector2 v) {
        mVelocity.set(v);
    }

    public Vector2 getVelocity() {
        return mVelocity;
    }

}

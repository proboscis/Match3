package com.glyph.java.particle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    public static final int RECORD_LENGTH = 10;
    private Sprite[] mRecord = new Sprite[RECORD_LENGTH];
    private int mCurrent = 0;

    public SpriteParticle() {
        //sCount++;
        //Gdx.app.log("SpriteParticle",""+sCount);
        mVelocity = new Vector2();
        for(int i = 0; i < RECORD_LENGTH; i++){
            mRecord[i] = new Sprite();
        }
    }

    @Override
    public void setRegion(TextureRegion region) {
        super.setRegion(region);
        for(int i = 0; i < RECORD_LENGTH; i++){
            setupRecord(i);
        }
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
        //setTexture(null);
        for(int i =0; i < RECORD_LENGTH;i++){
            setupRecord(i);
        }
        mVelocity.set(0, 0);
    }
    private void setupRecord(int i){
        Sprite current = mRecord[i];
        current.setPosition(getX(),getY());
        current.setSize(getWidth(), getHeight());
        current.setColor(getColor());
        current.setScale(getScaleX(), getScaleY());
        current.setOrigin(getOriginX(), getOriginY());
        current.setRotation(getRotation());
        current.setTexture(getTexture());
        current.setRegion(getRegionX(),getRegionY(),getRegionWidth(),getRegionHeight());
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
        setupRecord(mCurrent < RECORD_LENGTH ? mCurrent++ :(mCurrent=0));
        tmp.set(mVelocity);
        tmp.mul(delta);
        setPosition(getX() + tmp.x, getY() + tmp.y);
        tmp.set(0, 0);
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float alphaModulation) {
        int length = RECORD_LENGTH;
        float alpha = 1f/RECORD_LENGTH;
        for(int i = 0; i < length;i++){
            int index = i + mCurrent;
            index = index < length ? index:index-length;
            mRecord[index].draw(spriteBatch,i*alpha*alphaModulation);
        }
        super.draw(spriteBatch, alphaModulation);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        int length = RECORD_LENGTH;
        float alpha = 1f/RECORD_LENGTH;
        for(int i = 0; i < length;i++){
            int index = i + mCurrent;
            index = index < length ? index:index-length;
            mRecord[index].draw(spriteBatch,i*alpha);
        }
        super.draw(spriteBatch);
    }

    public void setVelocity(Vector2 v) {
        mVelocity.set(v);
    }

    public Vector2 getVelocity() {
        return mVelocity;
    }

}

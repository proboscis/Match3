package com.glyph.java.particle;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

/**
 * DURATION秒後に自動で自分をremoveしparticleもdisposeします
 *
 * @author glyph
 */
public class SpriteExplosion extends Actor implements Disposable {
    protected Queue<SpriteParticle> mParticles;
    protected ParticlePool<SpriteParticle> mPool;

    private static final float DURATION = 2;
    private static Vector2 tmp = new Vector2();
    float mGravity;
    float mPower;
    float mTimer;
    Vector2 mDirection;

    public SpriteExplosion(Sprite sprite, ParticlePool<SpriteParticle> pool) {
        mParticles = split(sprite, pool, 8, 8);
        mPool = pool;
        mGravity = 10;
        mPower = 30;
        mDirection = new Vector2();
        mTimer = DURATION;
        for (SpriteParticle sp : mParticles) {
            tmp.set(MathUtils.random(-1f, 1f), MathUtils.random(0f, 2f));
            tmp.nor();
            float pow = MathUtils.random(0f, mPower);
            tmp.mul(pow * pow);
            sp.setVelocity(tmp);
        }
    }

    public void act(float delta) {
        super.act(delta);
        mTimer -= delta;
        if (mTimer < 0) {
            dispose();
            remove();
            return;
        }
        for (SpriteParticle sp : mParticles) {
            sp.getVelocity().add(0f, -mGravity);
            sp.update(delta);
        }
    }

    private Queue<SpriteParticle> split(Sprite sprite, ParticlePool<SpriteParticle> pool, float divX, float divY) {
        LinkedList<SpriteParticle> result = new LinkedList<SpriteParticle>();

        int rx, ry, rw, rh, tw, th;
        float sx, sy, spx, spy, u, v, u2, v2;
        spx = sprite.getX();
        spy = sprite.getY();
        sx = sprite.getScaleX();
        sy = sprite.getScaleY();
        rx = sprite.getRegionX();
        ry = sprite.getRegionY();
        rw = sprite.getRegionWidth();
        rh = sprite.getRegionHeight();
        tw = sprite.getTexture().getWidth();
        th = sprite.getTexture().getHeight();

        float width = sprite.getWidth() * sx;
        float height = sprite.getHeight() * sy;
        float nX = (width / divX);
        float nY = (height / divY);
        float texDivX = ((float) rw / (float) tw / nX);
        float texDivY = ((float) rh / (float) th / nY);
        for (int x = 0; x < nX; x++) {
            for (int y = 0; y < nY; y++) {
                SpriteParticle sp = pool.obtain();
                TextureRegion region = new TextureRegion(sprite.getTexture());
                u = rx + texDivX * x;
                v = ry + texDivY * y;
                u2 = u + texDivX;
                v2 = v + texDivY;
                region.setRegion(u, v, u2, v2);
                //region.setRegion(texDivX,texDivY, 1, 1);

                sp.init(region);
                sp.setSize(divX, divY);
                sp.setPosition(spx + x * divX * sx, spy + height - y * divY * sy);

                result.add(sp);
            }
        }
        return result;
    }

    @Override
    public boolean remove() {
        return super.remove();
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        for (SpriteParticle sp : mParticles) {
            sp.draw(batch, mTimer / DURATION);
        }
    }

    @Override
    public void dispose() {
        while (!mParticles.isEmpty()) {
            mPool.free(mParticles.poll());
        }
    }
}

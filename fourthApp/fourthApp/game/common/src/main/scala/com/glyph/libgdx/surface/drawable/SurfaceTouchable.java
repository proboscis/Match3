package com.glyph.libgdx.surface.drawable;

import com.badlogic.gdx.math.Vector2;

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/11
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
public interface SurfaceTouchable {
    public boolean hit(Vector2 pos);
}

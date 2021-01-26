package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class AddOn {
    public Rectangle hitbox;

    // TODO position relative to ship?
    // e.g. add-on on left vs right side of ship?
    // we might decide to just generically increase size of ship hitbox, negating need for each
    // add-on to have its own
    public abstract void update(float delta);

    public abstract void draw(SpriteBatch spriteBatch);
}

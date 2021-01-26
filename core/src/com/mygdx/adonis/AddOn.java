package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;



public class AddOn {
    public Rectangle hitbox;

    // TODO position relative to ship?
    // e.g. add-on on left vs right side of ship?
    // we might decide to just generically increase size of ship hitbox, negating need for each
    // add-on to have its own
    private int id;
    private String name;

    public AddOn(int id, String name){
        this.id = id;
        this.name = name;
    }

//    public abstract void update(float delta);
//
//    public abstract void draw(SpriteBatch spriteBatch);
}
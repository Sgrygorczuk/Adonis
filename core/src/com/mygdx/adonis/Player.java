package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.mygdx.adonis.Consts.WORLD_HEIGHT;
import static com.mygdx.adonis.Consts.WORLD_WIDTH;

public class Player extends Ship {

    public AddOn selectedAddOn;

    public Player(Texture texture) {
        // spawn player near the bottom of the screen by default
        this(texture, WORLD_WIDTH / 2, WORLD_HEIGHT / 10);
    }

    public Player(Texture texture, float initX, float initY) {
        super(texture, initX, initY, Alignment.PLAYER);
    }

    @Override
    public void update(float delta) {
        updateBullets(delta);
        super.update(delta);
        // TODO update player state based on addons?
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        super.draw(spriteBatch);
    }

    public void ejectSelected() {
        if (selectedAddOn == null) return;
//        super.addOns.removeValue(selectedAddOn, true);
    }
}

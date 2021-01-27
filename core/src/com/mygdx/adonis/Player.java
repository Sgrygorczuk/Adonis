package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.mygdx.adonis.Consts.WORLD_HEIGHT;
import static com.mygdx.adonis.Consts.WORLD_WIDTH;

public class Player extends Ship {

    public AddOn selectedAddOn;

    public Player(TextureRegion[][] textureFly, TextureRegion[][] textureDie) {
        // spawn player near the bottom of the screen by default
        this(textureFly, textureDie, WORLD_WIDTH / 2, WORLD_HEIGHT / 10);
    }

    public Player(TextureRegion[][] textureFly, TextureRegion[][] textureDie, float initX, float initY) {
        super(textureFly, textureDie, initX, initY);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // TODO update player state based on addons?
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        super.draw(spriteBatch);
    }

    public void ejectSelected() {
        if (selectedAddOn == null) return;
        super.addOns.removeValue(selectedAddOn, true);
    }
}

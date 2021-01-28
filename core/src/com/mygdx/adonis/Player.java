package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.mygdx.adonis.Consts.WORLD_HEIGHT;
import static com.mygdx.adonis.Consts.WORLD_WIDTH;

public class Player extends Ship {

    public AddOn selectedAddOn;

    public Player(TextureRegion[][] textureFly, TextureRegion[][] textureDie, float initX, float initY) {
        super(textureFly, textureDie, initX, initY, Alignment.PLAYER);
    }

    @Override
    public void update(float delta) {
        updateBullets(delta);
        super.update(delta);
        if(hitbox.x < 95){hitbox.x = 95;}
        if(hitbox.x + hitbox.width> 380){hitbox.x = 380 - hitbox.width;}
        if(hitbox.y < 0){hitbox.y = 0;}
        if(hitbox.y + hitbox.height > WORLD_HEIGHT){hitbox.y = WORLD_HEIGHT - hitbox.height;}
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

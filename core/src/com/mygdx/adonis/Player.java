package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.mygdx.adonis.Consts.LEFT_BOUND;
import static com.mygdx.adonis.Consts.PLAYER_SPEED;
import static com.mygdx.adonis.Consts.RIGHT_BOUND;
import static com.mygdx.adonis.Consts.WORLD_HEIGHT;

public class Player extends Ship {

    public AddOn selectedAddOn;

    public Player(TextureRegion[][] textureFly, TextureRegion[][] textureDie, float initX, float initY) {
        super(textureFly, textureDie, initX, initY, Alignment.PLAYER);
        this.shipSpeed = PLAYER_SPEED;

    }

    @Override
    public void update(float delta) {
        this.shootTimer -= delta;
        updateBullets(delta);
        super.update(delta);
        if (hitbox.x < LEFT_BOUND) {
            hitbox.x = LEFT_BOUND;
        }
        if (hitbox.x + hitbox.width > RIGHT_BOUND) {
            hitbox.x = RIGHT_BOUND - hitbox.width;
        }
        if (hitbox.y < 0) {
            hitbox.y = 0;
        }
        if (hitbox.y + hitbox.height > WORLD_HEIGHT) {
            hitbox.y = WORLD_HEIGHT - hitbox.height;
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        super.draw(spriteBatch);
    }

    public void ejectSelected(int ind) {
        if(ind >= addOns.size) return;

        AddOnData destroyed = addOns.get(ind);
        System.out.println("Destroyed "+destroyed.getName()+" at index "+ind);
        addOns.removeIndex(ind);
        destroyedPart(destroyed);
        updateShipSpecs();
//        super.addOns.removeValue(selectedAddOn, true);
    }
}

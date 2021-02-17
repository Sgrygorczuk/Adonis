package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.mygdx.adonis.Consts.LEFT_BOUND;
import static com.mygdx.adonis.Consts.PLAYER_SPEED;
import static com.mygdx.adonis.Consts.RIGHT_BOUND;
import static com.mygdx.adonis.Consts.WORLD_HEIGHT;

public class Player extends Ship {

    public AddOn selectedAddOn;
    private Texture shield;

    public Player(TextureRegion[][] spriteSheet, Texture shield,
                  float initX, float initY) {
        super(spriteSheet, initX, initY, Alignment.PLAYER, false);
        this.shield = shield;
        this.shipSpeed = PLAYER_SPEED;
    }


    @Override
    public void update(float delta) {
        super.update(delta);
        this.shootTimer -= delta;
        updateBullets(delta);
        boundCheck();
    }

    private void boundCheck(){
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
        if(hasShield && !flashing && energyBurn <= 0f){spriteBatch.draw(shield, hitbox.x, hitbox.y, hitbox.width, hitbox.height);}
    }

    public boolean ejectSelected(int ind) {
        if(ind >= addOns.size) return false;

        AddOnData destroyed = addOns.get(ind);
        System.out.println("Destroyed "+destroyed.getObjectName()+" at index "+ind);
        addOns.removeIndex(ind);
        destroyedPart(destroyed);
        updateShipSpecs();
        return true;
//        super.addOns.removeValue(selectedAddOn, true);
    }
}

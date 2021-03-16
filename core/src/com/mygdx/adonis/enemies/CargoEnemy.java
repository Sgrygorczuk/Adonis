package com.mygdx.adonis.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.adonis.Alignment;
import com.mygdx.adonis.Ship;

import static com.mygdx.adonis.Consts.BULLET_DAMAGE;
import static com.mygdx.adonis.Consts.ENEMY_SPEED;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;
import static com.mygdx.adonis.Direction.DOWN;

public class CargoEnemy extends Ship {

    /**
     * Cardgo unit doesn't do much other than spawn addOns for player
     * @param spriteSheet texture
     * @param initX position
     * @param initY position
     */
    public CargoEnemy(TextureRegion[][] spriteSheet, float initX, float initY) {
        super(spriteSheet, initX, initY, Alignment.ENEMY, true, 1.4f, 20);
        this.shipSpeed = ENEMY_SPEED;
        this.velocity.y = TILE_HEIGHT;
        this.dir = DOWN;
        this.health = 60;
        this.maxHealth = 60;
    }

    /**
     * Update y position
     * @param delta timing var
     */
    @Override
    public void update(float delta) { super.update(delta); }
}

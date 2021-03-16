package com.mygdx.adonis.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.adonis.Alignment;
import com.mygdx.adonis.Ship;

import static com.mygdx.adonis.Consts.BULLET_DAMAGE;
import static com.mygdx.adonis.Consts.ENEMY_SPEED;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;
import static com.mygdx.adonis.Direction.DOWN;
import static com.mygdx.adonis.Direction.DOWN_L;
import static com.mygdx.adonis.Direction.DOWN_LEFT;
import static com.mygdx.adonis.Direction.DOWN_R;
import static com.mygdx.adonis.Direction.DOWN_RIGHT;

public class BaseEnemy extends Ship {

    //Timer counting down until we turn the draw function on/Off
    private static final float DIR_TIME = 61/55F;
    private float dirTimer = DIR_TIME;

    /**
     * Base enemy that the player will encounter
     * @param spriteSheet texture
     * @param initX position
     * @param initY position
     */
    public BaseEnemy(TextureRegion[][] spriteSheet, float initX, float initY) {
        super(spriteSheet, initX, initY, Alignment.ENEMY, true, 1f, 100);
        this.shipSpeed = ENEMY_SPEED;
        this.dir = DOWN;
        this.health = 20;
        this.maxHealth = 20;
    }

    /**
     * Update the shooting and position
     * @param delta timing var
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        this.shootTimer -= delta;
        changeDir(delta);
    }

    /**
     * Changes the direction when timer goes off
     * @param delta timing var
     */
    public void changeDir(float delta){
        dirTimer -= delta;
        if (dirTimer <= 0) {
            dirTimer = DIR_TIME;
            if(this.dir == DOWN_L){ this.dir = DOWN_R; }
            else{ this.dir = DOWN_L; }
        }
    }
}

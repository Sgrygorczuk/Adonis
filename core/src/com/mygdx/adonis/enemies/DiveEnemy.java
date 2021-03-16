package com.mygdx.adonis.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.adonis.Alignment;
import com.mygdx.adonis.Ship;

import static com.mygdx.adonis.Consts.ENEMY_SPEED;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.WORLD_WIDTH;
import static com.mygdx.adonis.Direction.DOWN_L;
import static com.mygdx.adonis.Direction.DOWN_LEFT;
import static com.mygdx.adonis.Direction.DOWN_R;
import static com.mygdx.adonis.Direction.DOWN_RIGHT;

public class DiveEnemy extends Ship {

    boolean switchFlag = false; //Tells is to fly left and right

    /**
     * Constructor
     * @param spriteSheet the sprite sheet
     * @param initX position
     * @param initY position
     */
    public DiveEnemy(TextureRegion[][] spriteSheet, float initX, float initY) {
        super(spriteSheet, initX, initY, Alignment.ENEMY, true, 0.9f, 50);
        this.shipSpeed = ENEMY_SPEED*1.2f;
        if(initX < WORLD_WIDTH/2f){
            this.dir = DOWN_RIGHT;
        }
        else{
            this.dir = DOWN_LEFT;
        }
        this.health = 10;
        this.maxHealth = 10;
    }

    /**
     * Update the shooting and moving of the ship
     * @param delta timing var
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        this.shootTimer -= delta;
        if(!switchFlag){changeDir();}
    }

    /**
     * Checks if it should go left or right then updates the positon
     */
    public void changeDir(){
        if(this.dir == DOWN_RIGHT && hitbox.x > WORLD_WIDTH/2f){
            this.dir = DOWN_LEFT;
            switchFlag = true;
        }
        else if(this.dir == DOWN_LEFT && hitbox.x < WORLD_WIDTH/2f){
            this.dir = DOWN_RIGHT;
            switchFlag = true;
        }

    }
}
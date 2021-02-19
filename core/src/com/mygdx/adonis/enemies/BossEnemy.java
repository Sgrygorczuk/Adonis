package com.mygdx.adonis.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.adonis.Alignment;
import com.mygdx.adonis.Ship;

import static com.mygdx.adonis.Consts.ENEMY_SPEED;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;

import static com.mygdx.adonis.Direction.NONE;

public class BossEnemy extends Ship {

    public BossEnemy(TextureRegion[][] spriteSheet, float initX, float initY) {
        super(spriteSheet, initX, initY, Alignment.BOSS, true, 15f, 1000);
        this.shipSpeed = ENEMY_SPEED;
        this.velocity.y = TILE_HEIGHT;
        this.dir = NONE;
        this.health = 500;
        this.maxHealth = 500;

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        this.shootTimer -= delta;
    }
}
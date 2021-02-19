package com.mygdx.adonis.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.adonis.Alignment;
import com.mygdx.adonis.Ship;

import static com.mygdx.adonis.Consts.BULLET_DAMAGE;
import static com.mygdx.adonis.Consts.ENEMY_SPEED;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;
import static com.mygdx.adonis.Direction.DOWN;

public class BaseEnemy extends Ship {

    public BaseEnemy(TextureRegion[][] spriteSheet, float initX, float initY) {
        super(spriteSheet, initX, initY, Alignment.ENEMY, true, 1f, 100);
        this.shipSpeed = ENEMY_SPEED;
        this.dir = DOWN;
        this.health = 20;
        this.maxHealth = 20;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        this.shootTimer -= delta;
    }
}

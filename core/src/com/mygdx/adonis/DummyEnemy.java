package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.mygdx.adonis.Consts.BULLET_DAMAGE;
import static com.mygdx.adonis.Consts.ENEMY_SPEED;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;
import static com.mygdx.adonis.Direction.DOWN;

public class DummyEnemy extends Ship {

    public DummyEnemy(TextureRegion[][] textureFly, TextureRegion[][] textureDie, float initX, float initY) {
        super(textureFly, textureDie, initX, initY, Alignment.ENEMY);
        this.shipSpeed = ENEMY_SPEED;
        this.velocity.y = TILE_HEIGHT;
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

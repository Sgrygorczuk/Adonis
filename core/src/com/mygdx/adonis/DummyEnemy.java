package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.mygdx.adonis.Consts.BULLET_DAMAGE;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;
import static com.mygdx.adonis.Direction.DOWN;

public class DummyEnemy extends Ship {

    public DummyEnemy(TextureRegion[][] textureFly, TextureRegion[][] textureDie, float initX, float initY) {
        super(textureFly, textureDie, initX, initY, Alignment.ENEMY);
        this.shipSpeed = 3;
        this.velocity.y = 3 * TILE_HEIGHT;
        this.dir = DOWN;
        this.health = 40;
        this.maxHealth = 40;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        this.shootTimer -= delta;
    }

    @Override
    public void fire() {
        // spawn a bullet
        // in theory this could push a "bullet fired" event to the event system but whatever
    }
}

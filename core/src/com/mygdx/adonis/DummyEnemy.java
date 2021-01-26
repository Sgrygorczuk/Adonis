package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;

import static com.mygdx.adonis.Consts.TILE_HEIGHT;

public class DummyEnemy extends Ship {

    private float shootTimer = 1;

    public DummyEnemy(Texture texture, float initX, float initY) {
        super(texture, initX, initY);
        this.velocity.y = -3 * TILE_HEIGHT;
    }

    @Override
    public void update(float delta) {
        shootTimer -= delta;
        if (shootTimer < 0) {
            shoot();
            shootTimer = 1;
        }

        super.update(delta);
    }

    public void shoot() {
        // spawn a bullet
        // in theory this could push a "bullet fired" event to the event system but whatever
    }
}

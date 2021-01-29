package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import static com.mygdx.adonis.Consts.ADD_ON_SPEED;
import static com.mygdx.adonis.Consts.ADD_ON_TILE;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;

public class AddOn {
    public final Rectangle hitbox;
    private final TextureRegion texture;
    public int id;

    public AddOn(TextureRegion texture, float x, float y, int id){
        this.texture = texture;
        this.id = id;
        hitbox = new Rectangle(x, y, ADD_ON_TILE, ADD_ON_TILE);
    }

    public void update(float delta){
        hitbox.y = hitbox.getY() + (-1 * delta * TILE_HEIGHT * ADD_ON_SPEED);
    }

    /**
     * Input: Shaperenderd
     * Output: Void
     * Purpose: Draws the circle on the screen using render
     */
    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }



}
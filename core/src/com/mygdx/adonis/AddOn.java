package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import static com.mygdx.adonis.Consts.ADD_ON_SPEED;
import static com.mygdx.adonis.Consts.ADD_ON_TILE;
import static com.mygdx.adonis.Consts.TILE_HEIGHT;

public class AddOn {
    public final Rectangle hitbox;              //HitBox
    private final TextureRegion texture;        //Texture
    public int id;                              //Id

    /**
     * Constructor for the class
     * @param texture that's used to draw it
     * @param x position
     * @param y position
     * @param id used to tell what kind of AddOn it is
     */
    public AddOn(TextureRegion texture, float x, float y, int id){
        this.texture = texture;
        this.id = id;
        hitbox = new Rectangle(x, y, ADD_ON_TILE, ADD_ON_TILE);
    }

    /**
     * Updates the y position of hte addon
     * @param delta timing variable
     */
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

    /**
     * Draws the texture of the AddOn
     * @param spriteBatch where the texture will be drawn
     */
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }



}
package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;

public class Bullet {
    public Rectangle hitbox;
    public Vector2 velocity;
    public int damage;
    public Direction dir;

    public TextureRegion[][] texture;
    private Animation<TextureRegion> animation;
    //Current animation frame time
    protected float animationTime = 0;

    public Alignment alignment;

    public Bullet(Alignment alignment, Direction dir, float x, float y, TextureRegion[][] texture, int damage) {
        this.alignment = alignment;
        this.dir = dir;
        this.hitbox = new Rectangle(x, y, TILE_WIDTH / 2f, TILE_HEIGHT / 2f);
        this.velocity = new Vector2(0, 0);
        this.texture = texture;
        this.damage = damage;
        setUpAnimation();
    }

    /**
     * Input: Void
     * Output: Void
     * Purpose: Sets up the animation loops in all of the directions
     */
    protected void setUpAnimation() {
        animation = new Animation<>(0.031f, this.texture[0][0], this.texture[0][1]);
        animation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void update(float delta) {
        if (this.alignment == Alignment.PLAYER) {
            this.velocity.y = 1;
        } else {
            this.velocity.y = -1;
        }

        hitbox.x = hitbox.getX() + dir.getX();
        hitbox.y = hitbox.getY() + dir.getY() * 10;

        animationTime += delta;
    }

    public void dispose() {
//        this.texture.dispose();
        this.alignment = null;
        this.dir = null;
        this.hitbox = null;
        this.velocity = null;

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
        TextureRegion currentFrame = animation.getKeyFrame(animationTime);
        spriteBatch.draw(currentFrame, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

}

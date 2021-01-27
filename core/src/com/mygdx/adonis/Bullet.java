package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;

public class Bullet {
    public Rectangle hitbox;
    public Vector2 velocity;
    public int damage;

    public TextureRegion[][] texture;
    private Animation animation;
    //Current animation frame time
    protected float animationTime = 0;

    public Alignment alignment;

    public Bullet(Alignment alignment, TextureRegion[][] texture) {
        this.alignment = alignment;
        this.texture = texture;
        setUpAnimation();
    }

    /**
     Input: Void
     Output: Void
     Purpose: Sets up the animation loops in all of the directions
     */
    protected void setUpAnimation(){
        animation= new Animation<>(0.25f, this.texture[0][0], this.texture[0][1]);
        animation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void update(float delta) {
        float newX = hitbox.getX() + (velocity.x * delta);
        float newY = hitbox.getY() + (velocity.y * delta);
        hitbox.setPosition(newX, newY);
    }

    public void draw(SpriteBatch spriteBatch){
        TextureRegion currentFrame = (TextureRegion) animation.getKeyFrame(animationTime);
        spriteBatch.draw(currentFrame, hitbox.x, hitbox.y, TILE_WIDTH/2f, TILE_HEIGHT/2f);
    }

}

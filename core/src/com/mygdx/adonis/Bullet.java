package com.mygdx.adonis;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static com.mygdx.adonis.Consts.BULLET_DRAW_OFFSET;
import static com.mygdx.adonis.Consts.BULLET_TILE_SIZE;

public class Bullet {
    public Rectangle hitbox; //Hit box
    public Vector2 velocity; //Bullets velocity
    public int damage;       //How much damage it gives out
    public Direction dir;    //Which direction it goes

    public TextureRegion[][] texture;  //Texture
    private Animation<TextureRegion> animation; //Animation
    protected float animationTime = 0;      //Current animation frame time

    public Alignment alignment; //Enemy or Player

    public Bullet(Alignment alignment, Direction dir, float x, float y, float xTowards, float yTowards, TextureRegion[][] texture, int damage) {
        this.alignment = alignment;
        this.dir = dir;
        this.hitbox = new Rectangle(x, y, BULLET_TILE_SIZE, BULLET_TILE_SIZE);
        this.velocity = new Vector2(0, 0);
        if (this.alignment == Alignment.PLAYER) {
            this.velocity.y = 1;
            velocity.x = 0;
        }
        else {
            if (y > yTowards) {
                this.velocity.x = -(xTowards - x) / (yTowards - y);
            }
            else{
                this.velocity.x = -(xTowards - x) / (y - yTowards);
            }
            if(velocity.x > 1){velocity.x = 1;}
            else if(velocity.x < -1){velocity.x = -1;}
            this.velocity.y = -3;
            System.out.println(velocity);
        }

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

    /**
     * Updates the animation and position of bullet
     * @param delta timing var
     */
    public void update(float delta) {
        hitbox.x += velocity.x;
        hitbox.y += velocity.y;

        animationTime += delta;
    }

    /**
     * The function that gets rid of things once bullet is dead
     */
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

    /**
     * Draws the texture of the bullet
     * @param spriteBatch where it will be drawn
     */
    public void draw(SpriteBatch spriteBatch) {
        TextureRegion currentFrame = animation.getKeyFrame(animationTime);
        spriteBatch.draw(currentFrame, hitbox.x-BULLET_DRAW_OFFSET/2f,
                hitbox.y-BULLET_DRAW_OFFSET/2f, hitbox.width+BULLET_DRAW_OFFSET
                , hitbox.height+BULLET_DRAW_OFFSET);
    }

}

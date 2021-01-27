package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;

public abstract class Ship {
    public Rectangle hitbox;
    public Vector2 velocity;
    public int health;
    public Direction dir;
    protected Array<AddOn> addOns;

    //Sprite sheet used
    protected TextureRegion[][] flySpriteSheet;
    protected TextureRegion[][] dieSpriteSheet;

    protected Animation flyAnimation;
    protected Animation dieAnimation;

    //Current animation frame time
    protected float animationTime = 0;

    public Ship(TextureRegion flySpriteSheet[][], TextureRegion[][] dieSpriteSheet, float initX, float initY) {
        // can multiply e.g. by 1.5, 1.2 to get more or less health
        this.health = 100;

        this.flySpriteSheet = flySpriteSheet;
        this.dieSpriteSheet = dieSpriteSheet;
        setUpAnimation();

        this.hitbox = new Rectangle(initX, initY, TILE_WIDTH, TILE_HEIGHT);
        this.velocity = new Vector2(0, 0);
        this.addOns = new Array<>();
    }

    /**
    Input: Void
    Output: Void
    Purpose: Sets up the animation loops in all of the directions
    */
    protected void setUpAnimation(){
        flyAnimation= new Animation<>(0.25f, this.flySpriteSheet[0][0], this.flySpriteSheet[0][1],
                this.flySpriteSheet[0][2], this.flySpriteSheet[0][3]);
        flyAnimation.setPlayMode(Animation.PlayMode.LOOP);

        dieAnimation= new Animation<>(0.1f, this.dieSpriteSheet[0][0], this.dieSpriteSheet[0][1],
                this.dieSpriteSheet[0][2], this.dieSpriteSheet[0][3], this.dieSpriteSheet[0][4], this.dieSpriteSheet[0][5],
                this.dieSpriteSheet[0][6], this.dieSpriteSheet[0][7], this.dieSpriteSheet[0][8]);
        dieAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    // collision isn't as simple as checking a single hitbox since each ship has multiple addons
    public boolean isColliding(Rectangle other) {
        // todo addons
        return this.hitbox.contains(other);
    }

    public void takeDamage(int amt) {
        this.health -= amt;
    }

    // TODO change velocity depending on game stuff
    public void update(float delta) {
        for (AddOn addOn : this.addOns) {
            addOn.update(delta);
        }

        animationTime += delta;

        float newX = hitbox.getX() + (velocity.x * delta);
        float newY = hitbox.getY() + (velocity.y * delta);

        hitbox.setPosition(newX, newY);
    }

    public void draw(SpriteBatch spriteBatch) {
        for (AddOn addOn : this.addOns) {
            addOn.draw(spriteBatch);
        }

        TextureRegion currentFrame = (TextureRegion) flyAnimation.getKeyFrame(animationTime);
        /*
        if (direction == 2) {
            currentFrame = (TextureRegion) backAnimation.getKeyFrame(animationTime);
        }
        else if (direction == 0) {
            currentFrame = (TextureRegion) rightAnimation.getKeyFrame(animationTime);
        }
        else if (direction == 1) {
            currentFrame = (TextureRegion) leftAnimation.getKeyFrame(animationTime);
        }
        */
        spriteBatch.draw(currentFrame, hitbox.x, hitbox.y, TILE_WIDTH, TILE_HEIGHT);
    }

    public void move(Direction dir) {
        this.dir = dir;
    }

    public void stop() {
        this.move(Direction.NONE);
    }
}

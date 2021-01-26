package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    protected Texture texture;

    public Ship(Texture texture, float initX, float initY) {
        // can multiply e.g. by 1.5, 1.2 to get more or less health
        this.health = 100;
        this.texture = texture;
        this.hitbox = new Rectangle(initX, initY, TILE_WIDTH, TILE_HEIGHT);
        this.velocity = new Vector2(0, 0);
        this.addOns = new Array<>();
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

        float newX = hitbox.getX() + (velocity.x * delta);
        float newY = hitbox.getY() + (velocity.y * delta);

        hitbox.setPosition(newX, newY);
    }

    public void draw(SpriteBatch spriteBatch) {
        for (AddOn addOn : this.addOns) {
             addOn.draw(spriteBatch);
        }

        spriteBatch.draw(this.texture, this.hitbox.x, this.hitbox.y, TILE_WIDTH, TILE_HEIGHT);
    }

    public void move(Direction dir) {
        this.dir = dir;
    }

    public void stop() {
        this.move(Direction.NONE);
    }
}

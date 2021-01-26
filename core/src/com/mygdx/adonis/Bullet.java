package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Bullet {
    public Rectangle hitbox;
    public Vector2 velocity;
    public int damage;

    public Texture texture;

    public Alignment alignment;

    public Bullet(Alignment alignment) {
        this.alignment = alignment;
    }

    public void update(float delta) {
        float newX = hitbox.getX() + (velocity.x * delta);
        float newY = hitbox.getY() + (velocity.y * delta);
        hitbox.setPosition(newX, newY);
    }
}

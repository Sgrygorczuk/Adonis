package com.mygdx.adonis;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static com.mygdx.adonis.Consts.TILE_HEIGHT;
import static com.mygdx.adonis.Consts.TILE_WIDTH;

public class Bullet {
    public Rectangle hitbox;
    public Vector2 velocity;
    public int damage;
    public Direction dir;

    public Texture texture;

    public Alignment alignment;

    public Bullet(Alignment alignment, Direction dir, float x, float y) {
        this.alignment = alignment;
        this.dir = dir;
        this.hitbox = new Rectangle(x,y,TILE_WIDTH, TILE_HEIGHT);
        this.velocity = new  Vector2(0,0);
    }

    public void update(float delta) {
        if(this.alignment == Alignment.PLAYER){
            this.velocity.y = 1;
        } else {
            this.velocity.y = -1;
        }
        float newX = hitbox.getX() + (velocity.x * delta*TILE_WIDTH*10);
        float newY = hitbox.getY() + (velocity.y * delta*TILE_HEIGHT*10);

        hitbox.setCenter(newX, newY);
    }

    public void dispose(){
//        this.texture.dispose();
        this.alignment = null;
        this.dir = null;
        this.hitbox = null;
        this.velocity = null;

    }
}

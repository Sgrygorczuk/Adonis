package com.mygdx.adonis;

public final class Consts {
    /*
    Dimensions -- Units the screen has
     */
    public static final float WORLD_WIDTH = 480;
    public static final float WORLD_HEIGHT = 320;

    public static final int LEFT_BOUND = 95;
    public static final int RIGHT_BOUND = 380;
    // TODO change to 32?
    // all entities are some multiple of these in size
    public static final float TILE_WIDTH = 16;
    public static final float TILE_HEIGHT = 16;

    public static final float ADD_ON_SPEED = 6;
    public static final int BULLET_DAMAGE = 10; // Default bullet damage
    public static final float BULLET_TILE_SIZE = TILE_HEIGHT/2f;
    public static final float BULLET_DRAW_OFFSET = 5;
    public static final float BULLET_SPAWN_TIME_START = 2f;
    public static final float BULLET_SPAWN_TIME_END = 2.5f;

    public static final float ADD_ON_TILE = TILE_HEIGHT/2f;

    public static final float ENEMY_SPEED = 6;
    public static final float PLAYER_SPEED = 12;

    public static final float DIAGONAL_SPEED = 0.707f; // Diagonal speed calculated

    public static final float ADD_ON_GROWTH = 0.15f; // How big the ship should grow

    public static final int BATTERY_SIZE = 500;
    public static final int SHIELD_MULTIPLIER = 20; // amt * shield_multiplier is the damage a shield takes
    public static final float ENERGY_RECHARGE = 2f; // Charger speed
    public static final float ENERGY_BURN_TIME = 5f;

}

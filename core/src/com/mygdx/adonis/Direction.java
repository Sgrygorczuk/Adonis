package com.mygdx.adonis;

import com.badlogic.gdx.math.MathUtils;

import static com.mygdx.adonis.Consts.DIAGONAL_SPEED;

public enum Direction {

    RIGHT(1, 0),
    LEFT(-1, 0),
    UP(0, 1),
    DOWN(0, -1),
    UP_RIGHT(DIAGONAL_SPEED, DIAGONAL_SPEED),
    UP_LEFT(-1 * DIAGONAL_SPEED, DIAGONAL_SPEED),
    DOWN_RIGHT(DIAGONAL_SPEED, -1 * DIAGONAL_SPEED),
    DOWN_LEFT(-1 * DIAGONAL_SPEED, -1 * DIAGONAL_SPEED),
    NONE(0, 0);

    private final float x;
    private final float y;

    public static Direction randomDir() {
        switch (MathUtils.random(3)) {
            case 0:
                return Direction.LEFT;
            case 1:
                return Direction.RIGHT;
            case 2:
                return Direction.UP;
            default:
                return Direction.DOWN;
        }
    }

    Direction(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}

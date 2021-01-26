package com.mygdx.adonis;

import com.badlogic.gdx.math.MathUtils;

public enum Direction {
    RIGHT,
    LEFT,
    UP,
    DOWN,
    UP_RIGHT,
    UP_LEFT,
    DOWN_RIGHT,
    DOWN_LEFT,
    NONE;

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

}

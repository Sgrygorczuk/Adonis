package com.mygdx.adonis.animation;

public class Animate {

    //Timer counting down until we turn the draw function on/Off
    private static final float FLASHING_TIME = 0.1F;
    private float flashingTimer = FLASHING_TIME;

    private void update(float delta) {
    flashingTimer -=delta;
    if(flashingTimer <=0)
    {
        flashingTimer = FLASHING_TIME;
    }
}
}

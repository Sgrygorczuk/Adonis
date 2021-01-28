package com.mygdx.adonis;

import com.badlogic.gdx.InputAdapter;

public class MouseInput extends InputAdapter {
    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

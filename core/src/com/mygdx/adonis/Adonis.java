package com.mygdx.adonis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.adonis.screen.LoadingScreen;

public class Adonis extends Game {

    private final AssetManager assetManager = new AssetManager();	//Holds the UI images and sound files

    /*
    Input: Boolean tells us if the game is started from Android or PC
    Output: Void
    Purpose: Tells the game what controls/information it should provide
    */
    public Adonis(){}

    /*
    Input: Void
    Output: Asset Manager
    Purpose: Returns asset manager with all its data
    */
    public AssetManager getAssetManager() { return assetManager; }

    /*
    Input: Void
    Output: Void
    Purpose: Starts the app
    */
    @Override
    public void create () {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        setScreen(new LoadingScreen(this));
    }

}
package com.mygdx.adonis.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.adonis.Adonis;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//TexturePacker.process("/home/sebastian/Projects/LibGDX_Personal/GreedyGobo/android/assets/Fonts", "/home/sebastian/Projects/LibGDX_Personal/GreedyGobo/android/assets", "font_assets");
		new LwjglApplication(new Adonis(), config);
	}
}

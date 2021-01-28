package com.mygdx.adonis;

import android.os.Bundle;
import android.view.WindowManager;;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.Timer;
import java.util.TimerTask;

public class AndroidLauncher extends AndroidApplication {

	//Timer and task used to save data
	Timer timer = new Timer();

	/*
	Input: Void
	Output: Void
	Purpose: Saves the data to the local storage
	*/
	TimerTask updateTask = new TimerTask() {
		@Override
		public void run() { }
	};

	/*
	Input: Void
	Output: Void
	Purpose: When app turns on it loads saved data, if there is no saved data makes saved data then loads it
	*/
	TimerTask loadTask = new TimerTask() {
		@Override
		public void run() { }
	};

	Adonis adonis = new Adonis();

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timer.schedule(loadTask, 0);									//Loads in data from the saved file
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keeps the screen lit up
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = true;										//Allows the use of Accelerometer readings
		config.useImmersiveMode = true;										//Makes the navigation bar go away
		initialize(adonis, config);										//Starts the game
		//Every 60 sec save the data to data base
		timer.scheduleAtFixedRate(updateTask, 3000, 6000);
	}
}

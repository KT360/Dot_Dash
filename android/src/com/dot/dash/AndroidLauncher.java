package com.dot.dash;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.dot.dash.DotDash;

import Objects.GameVariables;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		int startNumb = prefs.getInt("startNumb",0);
		startNumb++;
		editor.putInt("startNumb",startNumb);
		if(startNumb == 1)
		{
			GameVariables.can_show_tutorial = true;
		}else
		{
			GameVariables.can_show_tutorial = false;
		}
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new DotDash(), config);
	}
}

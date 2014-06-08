package pt.smart.thought.silence.please.activities;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SilencePleaseApplication 
	extends Application {

	private static final int DEFAULT_NR_DECIBELS = 80;
	private static final int DEFAULT_SHUTUP_DELAY = 2000;
	private SharedPreferences prefs;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public boolean isShutupEnabled() {
		return prefs.getBoolean("enable_shutup", false);
	}
	
	public int getNumberOfDecibelsAllowed() {
		String maxDecibelsString = prefs.getString("maxDecibels", Integer.toString(DEFAULT_NR_DECIBELS));
		return Integer.parseInt(maxDecibelsString);
	}
	
	public int getWaitTime() {
		String delay = prefs.getString("shutupdelay", Integer.toString(DEFAULT_SHUTUP_DELAY));
		return Integer.parseInt(delay);
	}
	
}

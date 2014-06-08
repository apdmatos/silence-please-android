package pt.smart.thought.silence.please.activities;

import java.util.Random;
import pt.smart.thought.silence.please.DecibelMeasure;
import pt.smart.thought.silence.please.IDecibelListener;
import pt.smart.thought.silence.please.R;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity implements IDecibelListener {

	private DecibelMeasure decibelMeasure;
	private MediaPlayer[] mediaPlayers;
	private SilencePleaseApplication application;
	
	// represents the date, until the sound is locked
	private long soundLockedUntil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		decibelMeasure = new DecibelMeasure();
		decibelMeasure.AddListener(this);
		mediaPlayers = new MediaPlayer[] {
				MediaPlayer.create(this, R.raw.quietplease),
				MediaPlayer.create(this, R.raw.fuckingshutup),
				MediaPlayer.create(this, R.raw.quietsound)
		};
		
		application = ((SilencePleaseApplication) getApplication());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_aboutDeveloper:
                openDeveloperScreen();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    	
    }

	@Override
	protected void onResume() {
		super.onResume();
		decibelMeasure.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		decibelMeasure.stop();
	}

	@Override
	public void updateDecibelValue(double decibel) {
		String text = Integer.toString((int)decibel);
		TextView textView = (TextView)findViewById(R.id.decibels);
		textView.setText(text);
		
		if(application.isShutupEnabled() == false) {
			// shutup sound is not enabled... do nothing
			return;
		}
		
		if(decibel > application.getNumberOfDecibelsAllowed()) {
			// check if we can play another sound
			long currentTime = System.currentTimeMillis();
			if(currentTime > soundLockedUntil) {
				Random r=new Random();
				int index = r.nextInt(3);
				
				soundLockedUntil = System.currentTimeMillis() 
						+ mediaPlayers[index].getDuration()
						+ application.getWaitTime();
				
				mediaPlayers[index].start();
			}
		}
	}
	
	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
	}

	private void openDeveloperScreen() {
		Intent intent = new Intent(this, AboutDeveloperActivity.class);
        startActivity(intent);
	}
}

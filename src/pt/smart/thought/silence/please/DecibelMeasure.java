package pt.smart.thought.silence.please;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaRecorder;
import android.os.Handler;

public class DecibelMeasure {
	
	private static final String TAG = DecibelMeasure.class.toString();
	private static final double EMA_FILTER = 0.6;
	private static double mEMA = 0.0;
	private static int DELAY = 300;
    private static final double AMP = 1;// Math.pow(1, 1);
	
	private ArrayList<IDecibelListener> listeners;
	private MediaRecorder recorder;
	private Timer timer;
	
	private final Handler handler = new Handler();
	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			double db = getSoundDb(AMP);
			if(db < 0) db = 0;
			notifyListeners(db);
		}
	};
	
	private class RefreshDecibelTask extends TimerTask {
		@Override
		public void run() {
			handler.post(runnable);
		}
	}
	
	public DecibelMeasure() {
		listeners = new ArrayList<IDecibelListener>();
	}
	
	public void AddListener(IDecibelListener listener) {
		this.listeners.add(listener);
	}
	
	public void RemoveListener(IDecibelListener listener) {
		listeners.remove(listener);
	}
	
    public void start(){
        if (recorder != null) {
        	return;
        }
        	
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null"); 
        
        try
        {           
            recorder.prepare();
            recorder.start();
        }catch (java.io.IOException ioe) {
            android.util.Log.e(TAG, "Error starting the recorder... " + ioe);

        }catch (java.lang.SecurityException e) {
            android.util.Log.e(TAG, "SecurityException when starting the recording... " + e);
        }
        
        // set up the timer to notify the listeners
        timer = new Timer();
		timer.schedule(new RefreshDecibelTask(), DELAY, DELAY);
    }
    
    public void stop() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
	
	private void notifyListeners(double value) {
		for (IDecibelListener listener : listeners) {
			listener.updateDecibelValue(value);
		}
	}
	
    private  double getSoundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    
    private double getAmplitude() {
        if (recorder != null) {
            return  (recorder.getMaxAmplitude());
        }
        else {
            return 0;
        }
    }
    
    private double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
	
}

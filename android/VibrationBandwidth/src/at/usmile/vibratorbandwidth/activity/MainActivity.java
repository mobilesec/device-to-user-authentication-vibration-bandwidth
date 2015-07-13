package at.usmile.vibratorbandwidth.activity;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.usmile.vibrationbandwidth.R;

/**
 * Activity for user triggering vibration patterns.
 * 
 * @author Rainhard Findling
 * @date 13 Jul 2015
 * @version 1
 */
public class MainActivity extends Activity {

	private static final String	TAG	= "MainActivity";

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.layout_activity_main);
		// vibration buttons

		float factor = 1.8f;
		float time = 33.333f;
		configureVibrationButton(this, R.id.button_trigger_vibration_1, ((int) (time * Math.pow(factor, 0))));
		configureVibrationButton(this, R.id.button_trigger_vibration_2, ((int) (time * Math.pow(factor, 1))));
		configureVibrationButton(this, R.id.button_trigger_vibration_3, ((int) (time * Math.pow(factor, 2))));
		configureVibrationButton(this, R.id.button_trigger_vibration_4, ((int) (time * Math.pow(factor, 3))));
	}

	private void configureVibrationButton(MainActivity _context, int _buttonId, int _i) {
		configureVibrationButton(_context, _buttonId, new long[] { 0, _i, _i, _i });
	}

	private void configureVibrationButton(Activity _context, final int _buttonId, final long[] _vibrationPattern) {
		Button button = (Button) _context.findViewById(_buttonId);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _v) {
				Log.d(TAG, "buttonTriggerVibration#OnClickListener(), _vibrationPattern: " + Arrays.toString(_vibrationPattern));
				Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				if (v.hasVibrator()) {
					v.vibrate(_vibrationPattern, -1);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}

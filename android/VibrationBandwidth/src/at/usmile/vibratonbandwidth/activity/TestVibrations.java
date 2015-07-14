package at.usmile.vibratonbandwidth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import at.usmile.vibrationbandwidth.R;
import at.usmile.vibrationbandwidth.util.VibrationUtil;

/**
 * Activity for user triggering vibration patterns for testing purposes.
 * 
 * @author Rainhard Findling
 * @date 13 Jul 2015
 * @version 1
 */
public class TestVibrations extends Activity {
	private static final String	TAG	= TestVibrations.class.getSimpleName();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_vibration_test, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_goto_vibration_recognition: {
				Log.d(TAG, "action_goto_vibration_recognition");
				Intent intent = new Intent(this, RecognizeVibrations.class);
				startActivity(intent);
				return true;
			}

			case R.id.action_goto_settings: {
				Log.d(TAG, "action_goto_settings");
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			}

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.layout_activity_test_vibrations);

		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_1, 1);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_2, 1, 1);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_3, 1, 2);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_4, 1, 3);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_5, 2);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_6, 2, 1);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_7, 2, 2);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_8, 2, 3);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_9, 3);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_10, 3, 1);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_11, 3, 2);
		VibrationUtil.configureVibrationButton(this, R.id.button_trigger_vibration_12, 3, 3);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}

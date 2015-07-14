package at.usmile.vibratonbandwidth.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import at.usmile.vibrationbandwidth.R;
import at.usmile.vibrationbandwidth.sharedprefs.SharedPrefs;

/**
 * Activity for application settings.
 * 
 * @author Rainhard Findling
 * @date 14 Jul 2015
 * @version 1
 */
public class SettingsActivity extends Activity {
	private static final String	TAG	= SettingsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.layout_activity_settings);

		// VIBRATION RECOGNITION TEST LENGTH
		final EditText edittextVibrationRecognitionTestLength = (EditText) findViewById(R.id.edittext_vibration_recognition_test_length);
		edittextVibrationRecognitionTestLength.setText("" + SharedPrefs.getVibrationRecognitionTestLength(this));
		edittextVibrationRecognitionTestLength.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _s, int _start, int _before, int _count) {
			}

			@Override
			public void beforeTextChanged(CharSequence _s, int _start, int _count, int _after) {
			}

			@Override
			public void afterTextChanged(Editable _s) {
				try {
					int val = Integer.parseInt(edittextVibrationRecognitionTestLength.getText().toString());
					getSharedPreferences(SharedPrefs.SHARED_PREFENCES_ID, Context.MODE_PRIVATE).edit()
							.putInt(SharedPrefs.VIBRATION_RECOGNITION_TEST_LENGTH, val).commit();
				} catch (NumberFormatException e) {
				} catch (NullPointerException e) {
				}
			}
		});

		// VIBRATION RECOGNITION MIN REQ PLAYS
		final EditText edittextMinReqPlays = (EditText) findViewById(R.id.edittext_vibration_recognition_min_req_pattern_plays);
		edittextMinReqPlays.setText("" + SharedPrefs.getVibrationRecognitionMinReqPatternRepeats(this));
		edittextMinReqPlays.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _s, int _start, int _before, int _count) {
			}

			@Override
			public void beforeTextChanged(CharSequence _s, int _start, int _count, int _after) {
			}

			@Override
			public void afterTextChanged(Editable _s) {
				try {
					int val = Integer.parseInt(edittextVibrationRecognitionTestLength.getText().toString());
					getSharedPreferences(SharedPrefs.SHARED_PREFENCES_ID, Context.MODE_PRIVATE).edit()
							.putInt(SharedPrefs.VIBRATION_RECOGNITION_MIN_REQ_PATTERN_REPEATS, val).commit();
				} catch (NumberFormatException e) {
				} catch (NullPointerException e) {
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu items for use in the action bar
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.menu_vibration_recognition, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle presses on the action bar items
	// switch (item.getItemId()) {
	// case R.id.action_goto_vibration_test:
	// Log.d(TAG, "action_goto_vibration_test");
	// Intent intent = new Intent(this, TestVibrations.class);
	// startActivity(intent);
	// return true;
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }

}

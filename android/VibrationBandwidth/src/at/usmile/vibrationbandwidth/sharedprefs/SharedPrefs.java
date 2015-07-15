package at.usmile.vibrationbandwidth.sharedprefs;

import android.content.Context;

/**
 * Provides access to shared preferences
 * 
 * @author Rainhard Findling
 * @date 14 Jul 2015
 * @version 1
 */
public abstract class SharedPrefs {

	// LOAD DATA - EXAMPLE
	// getActivity().getSharedPreferences(SharedPrefs.SHARED_PREFENCES_ID,
	// Context.MODE_PRIVATE).getBoolean(SharedPrefs.USE_ENERGY_NORMALIZATION,
	// false);

	// WRITE DATA - EXAMPLE
	// getActivity().getSharedPreferences(SharedPrefs.SHARED_PREFENCES_ID,
	// Context.MODE_PRIVATE).edit()
	// .putBoolean(SharedPrefs.USE_ENERGY_NORMALIZATION, _isChecked).commit();

	// ==============================================================================================================
	// STATICS

	// ==============================================================================================================
	// SHARED PREFERENCE STRINGS

	public static final String	SHARED_PREFENCES_ID								= "at.usmile.vibrationbandwidth";

	/** how much samples a vibration recognition test contains at minimum. */
	public static final String	VIBRATION_RECOGNITION_TEST_LENGTH				= SHARED_PREFENCES_ID
																						+ ".vibration_recognition_test_length";
	/**
	 * how often users have to repeat the chosen vibration recognition sample at
	 * least
	 */
	public static final String	VIBRATION_RECOGNITION_MIN_REQ_PATTERN_REPEATS	= SHARED_PREFENCES_ID
																						+ ".vibration_recognition_min_req_pattern_repeats";

	// ==============================================================================================================
	// READ ACCESS

	public static int getVibrationRecognitionTestLength(Context _context) {
		return _context.getSharedPreferences(SharedPrefs.SHARED_PREFENCES_ID, Context.MODE_PRIVATE).getInt(
				SharedPrefs.VIBRATION_RECOGNITION_TEST_LENGTH, 12);
	}

	public static int getVibrationRecognitionMinReqPatternRepeats(Context _context) {
		return _context.getSharedPreferences(SharedPrefs.SHARED_PREFENCES_ID, Context.MODE_PRIVATE).getInt(
				SharedPrefs.VIBRATION_RECOGNITION_MIN_REQ_PATTERN_REPEATS, 5);
	}

}

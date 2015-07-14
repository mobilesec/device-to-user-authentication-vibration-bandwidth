package at.usmile.vibrationbandwidth.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.usmile.vibrationbandwidth.R;

import com.opencsv.CSVWriter;

/**
 * Some vibration functionality across activities.
 * 
 * @author Rainhard Findling
 * @date 14 Jul 2015
 * @version 1
 */
public class VibrationUtil {

	private static final String	TAG						= VibrationUtil.class.getSimpleName();

	private static Random		RANDOM					= new Random();

	private static long			VIBRATION_START_DELAY	= 70L;									// ms
	private static long			VIBRATION_DURATION		= 60L;									// ms
	private static long			VIBRATION_SHORT_PAUSE	= 60L;									// ms
	private static long			VIBRATION_LONG_PAUSE	= 200L;								// ms

	// ================================================================================================================
	// METHODS

	public static void configureVibrationButton(Activity _activity, int _buttonId, String _buttonText, int... _vibratorPattern) {
		long[] l = generateVibrationPattern(_vibratorPattern);
		VibrationUtil.configureVibrationButton(_activity, _buttonId, l, _buttonText);
	}

	public static long[] generateVibrationPattern(int[] _vibratorPattern) {
		List<Long> list = new ArrayList<Long>(15) {
			{
				add(VIBRATION_START_DELAY); // turn on immediately
			}
		};
		for (int i = 0; i < _vibratorPattern.length; i++) {
			for (int i2 = 0; i2 < _vibratorPattern[i]; i2++) {
				list.add(VIBRATION_DURATION); // turn off after...
				if (i2 + 1 == _vibratorPattern[i]) {
					// last entry of group
					if (i + 1 == _vibratorPattern.length) {
						// last overall entry, don't turn on again
					} else {
						list.add(VIBRATION_LONG_PAUSE); // turn back on after...
					}
				} else {
					// not last value of group
					list.add(VIBRATION_SHORT_PAUSE); // turn back on after...
				}
			}
		}
		long[] l = new long[list.size()];
		for (int i = 0; i < l.length; i++) {
			l[i] = list.get(i);
		}
		return l;
	}

	public static void configureVibrationButton(Activity _activity, int _buttonId, int... _vibratorPattern) {
		String patternString = "";
		for (int i = 0; i < _vibratorPattern.length; i++) {
			patternString += (" " + Integer.toString(_vibratorPattern[i]));
		}
		String buttonString = String.format(_activity.getResources().getText(R.string.trigger_vibration).toString(),
				patternString, "");
		configureVibrationButton(_activity, _buttonId, buttonString, _vibratorPattern);
	}

	public static void configureVibrationButton(final Activity _activity, final int _buttonId, final long[] _vibrationPattern,
			String _buttonString) {
		Button button = (Button) _activity.findViewById(_buttonId);
		button.setText(_buttonString);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _v) {
				vibrate(_activity, _vibrationPattern);
			}
		});
	}

	public static int[] generateRandomPattern2Groups3Vibrations() {
		int r1 = RANDOM.nextInt(3) + 1;
		int r2 = RANDOM.nextInt(4);
		if (r2 == 0) {
			return new int[] { r1 };
		}
		return new int[] { r1, r2 };
	}

	public static void vibrate(final Activity _activity, final long[] _vibrationPattern) {
		Vibrator v = (Vibrator) _activity.getSystemService(Context.VIBRATOR_SERVICE);
		if (v.hasVibrator()) {
			v.vibrate(_vibrationPattern, -1);
		}
	}

	public static void storeRecords(File _destinationFile, String filePath, int[] _pattern, List<int[]> _randomPattern,
			List<Boolean> _userAnswersCorrect, List<Boolean> _userAnswers) throws IOException {
		CSVWriter writer;
		// File exist
		if (_destinationFile.exists() && !_destinationFile.isDirectory()) {
			FileWriter fileWriter = new FileWriter(filePath, false); // overwrite
			writer = new CSVWriter(fileWriter);
		} else {
			writer = new CSVWriter(new FileWriter(filePath));
		}
		String[] data = { "Pattern", "Answer", "Correct" };
		writer.writeNext(data);
		for (int i = 0; i < _randomPattern.size(); i++) {
			int[] r = _randomPattern.get(i);
			data = new String[] { r[0] + "" + (r.length > 1 ? r[1] : ""), Boolean.toString(_userAnswers.get(i)),
					Boolean.toString(_userAnswersCorrect.get(i)) };
			writer.writeNext(data);
		}
		writer.close();
	}
}

package at.usmile.vibratonbandwidth.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import at.usmile.vibrationbandwidth.R;
import at.usmile.vibrationbandwidth.sharedprefs.SharedPrefs;
import at.usmile.vibrationbandwidth.util.VibrationUtil;

/**
 * Activity for user recognising vibrations.
 * 
 * @author Rainhard Findling
 * @date 14 Jul 2015
 * @version 1
 */
public class RecognizeVibrations extends Activity {
	private static final String	TAG	= RecognizeVibrations.class.getSimpleName();

	public enum VibrationState {
		CHOOSE_PATTERN, PLAY_PATTERN
	}

	private VibrationState	mVibrationState				= VibrationState.CHOOSE_PATTERN;

	/** the vibration pattern we're testing the user for. */
	private int[]			mVibrationPattern			= null;

	/** how often the chosen vibration pattern has been played by the user */
	private int				mAmountChosenPatternPlayed	= 0;

	/** amount of pattern that have been recognised. -1 means */
	private Integer			mAmountRandomPatternPlayed	= null;

	/** list of random pattern created for which user was asked */
	private List<int[]>		mRandomPattern				= null;
	/**
	 * if user was able to correctly recognize if the random pattern was the
	 * learned pattern or not
	 */
	private List<Boolean>	mUserAnswersCorrect			= null;
	private List<Boolean>	mUserAnswers				= null;

	// UI
	private Button			mButtonNewRandomPattern;
	private Button			mButtonPlayChosenPattern;
	private Button			mButtonRecognizePattern;

	private boolean			mChosenPatternPlayed		= false;

	private TextView		mTextViewStatus;

	private TextView		mTextViewChosenVibration;

	private String			mAnswerFeedback				= "";

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.layout_activity_recognize_vibrations);

		// UI
		mTextViewChosenVibration = (TextView) findViewById(R.id.textview_chosen_vibration);
		mTextViewStatus = (TextView) findViewById(R.id.textview_status);
		mButtonNewRandomPattern = (Button) findViewById(R.id.button_new_random_pattern);
		mButtonNewRandomPattern.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _v) {
				Log.d(TAG, "mButtonNewRandomPattern.onClick");
				mVibrationPattern = VibrationUtil.generateRandomPattern2Groups3Vibrations();
				mTextViewChosenVibration.setText("Created vibration: " + mVibrationPattern[0]
						+ (mVibrationPattern.length > 1 ? " " + mVibrationPattern[1] : ""));
				mVibrationState = VibrationState.PLAY_PATTERN;
				updateUi();
			}
		});
		mButtonPlayChosenPattern = (Button) findViewById(R.id.button_play_chosen_pattern);
		mButtonPlayChosenPattern.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _v) {
				Log.d(TAG, "mButtonPlayChosenPattern.onClick");
				VibrationUtil.vibrate(RecognizeVibrations.this, VibrationUtil.generateVibrationPattern(mVibrationPattern));
				++mAmountChosenPatternPlayed;
				mChosenPatternPlayed = true;
				updateUi();
			}
		});
		mButtonRecognizePattern = (Button) findViewById(R.id.button_recognize_pattern);
		mButtonRecognizePattern.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _v) {
				Log.d(TAG, "mButtonRecognizePattern.onClick");
				if (mAmountRandomPatternPlayed == null) {
					mUserAnswersCorrect = new ArrayList<Boolean>();
					mUserAnswers = new ArrayList<Boolean>();
					mRandomPattern = new ArrayList<int[]>();
					mAmountRandomPatternPlayed = 0;
				}
				++mAmountRandomPatternPlayed;
				mChosenPatternPlayed = false;
				// generate random pattern, vibrate
				final int[] randomVibrationPattern = VibrationUtil.generateRandomPattern2Groups3Vibrations();
				VibrationUtil.vibrate(RecognizeVibrations.this, VibrationUtil.generateVibrationPattern(randomVibrationPattern));
				// ask what user thinks
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								// Yes button clicked
								mRandomPattern.add(randomVibrationPattern);
								mUserAnswersCorrect.add(Arrays.equals(mVibrationPattern, randomVibrationPattern));
								mUserAnswers.add(true);
								mAnswerFeedback = Arrays.equals(mVibrationPattern, randomVibrationPattern) ? "Correct decision! "
										: "Unfortunately wrong decision. ";
								vibrationRecognizedCleanup();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								mRandomPattern.add(randomVibrationPattern);
								mUserAnswersCorrect.add(!Arrays.equals(mVibrationPattern, randomVibrationPattern));
								mUserAnswers.add(false);
								mAnswerFeedback = !Arrays.equals(mVibrationPattern, randomVibrationPattern) ? "Correct decision! "
										: "Unfortunately wrong decision. ";
								vibrationRecognizedCleanup();
								break;
						}
					}
				};
				new AlertDialog.Builder(RecognizeVibrations.this)
						.setMessage(getResources().getText(R.string.is_chosen_pattern_question))
						.setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
			}
		});
		updateUi();
	}

	public void vibrationRecognizedCleanup() {
		// terminate if we have enough
		if (mAmountRandomPatternPlayed >= SharedPrefs.getVibrationRecognitionTestLength(this)) {
			// count amount of correct user decisions
			int correctDecisions = 0;
			for (int i = 0; i < mUserAnswersCorrect.size(); i++) {
				if (mUserAnswersCorrect.get(i)) {
					++correctDecisions;
				}
			}
			// notify user that test has ended, ask for label, save
			// records
			final EditText editText = new EditText(RecognizeVibrations.this);
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
			editText.setHint("personId-tryId");
			android.content.DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case DialogInterface.BUTTON_NEUTRAL:
							String label = editText.getText().toString();
							// store data
							String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
							String fileName = "VibrationRecognition_" + label + "_" + mVibrationPattern[0] + ""
									+ (mVibrationPattern.length > 1 ? mVibrationPattern[1] : "") + ".csv";
							String filePath = baseDir + File.separator + fileName;
							File file = new File(filePath);
							Log.d(TAG, "writing to" + filePath + " into " + fileName);
							try {
								VibrationUtil.storeRecords(file, filePath, mVibrationPattern, mRandomPattern,
										mUserAnswersCorrect, mUserAnswers);
							} catch (IOException e) {
								e.printStackTrace();
								new RuntimeException(e);
							}
							// reset data
							mTextViewChosenVibration.setText("");
							mVibrationPattern = null;
							mAmountRandomPatternPlayed = null;
							mUserAnswersCorrect = null;
							mRandomPattern = null;
							mVibrationState = VibrationState.CHOOSE_PATTERN;
							updateUi();
							break;
					}
				}
			};
			new AlertDialog.Builder(RecognizeVibrations.this)
					.setView(editText)
					.setMessage(
							String.format(getResources().getText(R.string.enter_data_label).toString(), correctDecisions,
									mUserAnswersCorrect.size())).setNeutralButton("Save", dialogClickListener).show();
		}
		updateUi();
	}

	public void updateUi() {
		switch (mVibrationState) {
			case CHOOSE_PATTERN:
				mButtonNewRandomPattern.setEnabled(true);
				mButtonPlayChosenPattern.setEnabled(false);
				mButtonRecognizePattern.setEnabled(false);
				mTextViewStatus.setText(getResources().getText(R.string.status_create_random_pattern));
				break;
			case PLAY_PATTERN:
				mButtonNewRandomPattern.setEnabled(false);
				mButtonPlayChosenPattern.setEnabled(true);
				mButtonRecognizePattern.setEnabled(mAmountChosenPatternPlayed >= SharedPrefs
						.getVibrationRecognitionMinReqPatternRepeats(this) && mChosenPatternPlayed);
				if (mAmountChosenPatternPlayed < SharedPrefs.getVibrationRecognitionMinReqPatternRepeats(this)) {
					mTextViewStatus.setText(String.format(
							getResources().getText(R.string.status_learn_random_pattern).toString(),
							(SharedPrefs.getVibrationRecognitionMinReqPatternRepeats(this) - mAmountChosenPatternPlayed)));
				} else {
					if (!mChosenPatternPlayed) {
						mTextViewStatus.setText(String.format(getResources().getText(R.string.status_repeat_random_pattern)
								.toString(), mAnswerFeedback, ""
								+ (SharedPrefs.getVibrationRecognitionTestLength(this) - mAmountRandomPatternPlayed)));
					} else {
						mTextViewStatus.setText(getResources().getText(R.string.status_repeat_or_recognize));
					}
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_vibration_recognition, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_goto_vibration_test:
				Log.d(TAG, "action_goto_vibration_test");
				Intent intent = new Intent(this, TestVibrations.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}

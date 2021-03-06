/*
 * Programme done by Lucas Grzegorczyk
 * for AP course @ SDU.
 */

package pl.Furai.OneArmedBandit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Game extends Activity implements OnClickListener,
		OnSharedPreferenceChangeListener {
	// Prepare some global variables to later use them in code
	private static final String TAG = "GameAcitvity";

	private MyThread mThread = null;
	private MyHandler mHandler = new MyHandler();

	// No need for initialisation here - global variables are automatically
	// initialised
	private ImageView image0, image1, image2;
	private ConstantState frame0, frame1, frame2;
	private Button btnStart;
	private AnimationDrawable animation0, animation1, animation2;
	private SharedPreferences prefs;
	private TextView lblBetValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// Getting image views objects
		image0 = (ImageView) findViewById(R.id.ImageView0);
		image1 = (ImageView) findViewById(R.id.ImageView1);
		image2 = (ImageView) findViewById(R.id.ImageView2);

		// Getting button object
		btnStart = (Button) findViewById(R.id.ButtonStart);

		// Getting label
		lblBetValue = (TextView) findViewById(R.id.txtBetValue);

		// Setting animation as background
		image0.setBackgroundResource(R.anim.fruits1);
		image1.setBackgroundResource(R.anim.fruits2);
		image2.setBackgroundResource(R.anim.fruits3);

		// Getting animation object
		animation0 = (AnimationDrawable) image0.getBackground();
		animation1 = (AnimationDrawable) image1.getBackground();
		animation2 = (AnimationDrawable) image2.getBackground();
		/*
		 * Don't run animations from the OnCreate
		 * 
		 * spinAnimation0.start(); spinAnimation1.start();
		 * spinAnimation2.start();
		 */

		// Setting on click listeners
		image0.setOnClickListener(this);
		image1.setOnClickListener(this);
		image2.setOnClickListener(this);
		btnStart.setOnClickListener(this);

		// Getting shared preferences object.
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		lblBetValue.setText(String.format(
				getResources().getString(R.string.lblBetChosen),
				prefs.getString("betValue", "Not set")));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) { //
		case R.id.action_settings:
			startActivity(new Intent(this, SetPreferenceActivity.class)); //
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ButtonStart:
			Log.v(TAG, "Clicked the start button!");
			if (!animation0.isRunning() && !animation1.isRunning()
					&& !animation2.isRunning())
				startAll();
			break;
		case R.id.ImageView0:
			Log.v(TAG, "Clicked the first image!");
			if (animation0.isRunning())
				animation0.stop();
			else
				Log.v(TAG, "Clicked the first image! Cannot stop.");
			break;
		case R.id.ImageView1:
			Log.v(TAG, "Clicked the second image!");
			if (animation1.isRunning())
				animation1.stop();
			else
				Log.v(TAG, "Clicked the second image! Cannot stop.");
			break;
		case R.id.ImageView2:
			Log.v(TAG, "Clicked the third image!");
			if (animation2.isRunning())
				animation2.stop();
			else
				Log.v(TAG, "Clicked the third image! Cannot stop.");
			break;

		}

		if (!animation0.isRunning() && !animation1.isRunning()
				&& !animation2.isRunning()) {
			allStopped();
			Log.v(TAG, "Stopped all animations.");
			if (mThread != null) {
				mThread.interrupt();
				mThread = null;
			}
		}

	}

	private class MyThread extends Thread {
		private static final String TAG = "Thread";

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Log.v(TAG, "New thread.");
			try {
				Message msg;
				Bundle bundle = new Bundle();

				for (int i = 0; i < 3; i++) {
					msg = mHandler.obtainMessage();
					bundle.putInt("stop", i);
					msg.setData(bundle);
					Thread.sleep(8000 - i * 3000);
					Log.v(TAG, "Msg: " + bundle);
					msg.sendToTarget();
					// Sending a message takes some time... you have to wait
					// because otherwise value of i gets changed in the bundle
					// Need some way to synchronise this call ...
					Log.v(TAG, "Value of i: " + i);
				}
			} catch (InterruptedException e) {
				Log.v(TAG, "Interrupted, stopping thread. ");
			}

		}

	}

	private class MyHandler extends Handler {
		private static final String TAG = "Handler";

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.v(TAG, "Handling message.");

			// It calls a click on the corresponding image
			switch (msg.getData().getInt("stop")) {
			case 0:
				image0.performClick();
				break;
			case 1:
				image1.performClick();
				break;
			case 2:
				image2.performClick();
				break;
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

		// When preferences change - display toast message
		showToastShort(String.format(
				getResources().getString(R.string.ToastBetChosen),
				prefs.getString(key, "Not set")));
		lblBetValue.setText(String.format(
				getResources().getString(R.string.lblBetChosen),
				prefs.getString(key, "Not set")));

	}

	public void allStopped() {

		frame0 = animation0.getCurrent().getConstantState();
		frame1 = animation1.getCurrent().getConstantState();
		frame2 = animation2.getCurrent().getConstantState();
		Log.v(TAG, String.format("ID of first image: %s", frame0));
		Log.v(TAG, String.format("ID of second image: %s", frame1));
		Log.v(TAG, String.format("ID of third image: %s", frame2));
		if (frame0 == frame1 && frame1 == frame2) {
			Log.v(TAG, "3 matches found");
			showToastShort(String
					.format(getResources().getString(R.string.Equal), 3,
							(Integer.parseInt((prefs.getString("betValue",
									"Not set")))) * 50));
		} else if (frame0 == frame1 || frame1 == frame2 || frame2 == frame0) {
			Log.v(TAG, "2 matches found");
			showToastShort(String
					.format(getResources().getString(R.string.Equal), 2,
							(Integer.parseInt((prefs.getString("betValue",
									"Not set")))) * 5));
		} else {
			Log.v(TAG, "0 matches found");
			showToastShort(getResources().getString(R.string.NoEqual));
		}
	}

	private void showToastShort(String msg) {
		Toast.makeText(Game.this, msg, Toast.LENGTH_SHORT).show();
	}

	private void startAll() {
		animation0.start();
		animation1.start();
		animation2.start();
		if (mThread == null) {
			mThread = new MyThread();
			mThread.start();
		}
	}
}

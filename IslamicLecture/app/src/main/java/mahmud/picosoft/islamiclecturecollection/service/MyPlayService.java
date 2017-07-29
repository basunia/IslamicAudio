package mahmud.picosoft.islamiclecturecollection.service;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.IBinder;
//import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import mahmud.picosoft.islamiclecturecollection.activity.LecturesList;
import mahmud.picosoft.islamiclecturecollection.R;


/* This file contains the source code for examples discussed in Tutorials 1-9 of developerglowingpigs YouTube channel.
 *  The source code is for your convenience purposes only. The source code is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*/


public class MyPlayService extends Service implements OnCompletionListener,
		OnPreparedListener, OnErrorListener, OnSeekCompleteListener,
		OnInfoListener, OnBufferingUpdateListener {

	private static final String TAG = "TELSERVICE";
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private String sntAudioLink;
	
	//-- URL location of audio clip PUT YOUR AUDIO URL LOCATION HERE ---
	private static final String URL_STRING = "http://www.glowingpigs.com/audioclip/";
	
	// Set up the notification ID
	private static final int NOTIFICATION_ID = 1;
	private boolean isPausedInCall = false;
	private PhoneStateListener phoneStateListener;
	private TelephonyManager telephonyManager;

	// ---Variables for seekbar processing---
	String sntSeekPos;
	int intSeekPos;
	int mediaPosition;
	int mediaMax;
	//Intent intent;
	private final Handler handler = new Handler();
	private static int songEnded;
	public static final String BROADCAST_ACTION = "mahmud.picosoft.islamiclecturecollection.seekprogress";

	// Set up broadcast identifier and intent
	public static final String BROADCAST_BUFFER = "mahmud.picosoft.islamiclecturecollection.broadcastbuffer";
	
	Intent bufferIntent;
	Intent seekIntent;

	// Declare headsetSwitch variable
	private int headsetSwitch = 1;
	
	///
	int lectureLength;

	// OnCreate
	@Override
	public void onCreate() {
		Log.v(TAG, "Creating Service");
		// android.os.Debug.waitForDebugger();
		// Instantiate bufferIntent to communicate with Activity for progress
		// dialogue
		bufferIntent = new Intent(BROADCAST_BUFFER);
		// ---Set up intent for seekbar broadcast ---
		seekIntent = new Intent(BROADCAST_ACTION);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.reset();

		// Register headset receiver
		registerReceiver(headsetReceiver, new IntentFilter(
				Intent.ACTION_HEADSET_PLUG));

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// ---Set up receiver for seekbar change ---
		registerReceiver(broadcastReceiver, new IntentFilter(
				LecturesList.BROADCAST_SEEKBAR));
		
		//pause broadcast
		registerReceiver(broadcastPause, new IntentFilter(LecturesList.BROADCAST_PAUSE));

		// Manage incoming phone calls during playback. Pause mp on incoming,
		// resume on hangup.
		// -----------------------------------------------------------------------------------
		// Get the telephony manager
		Log.v(TAG, "Starting telephony");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		Log.v(TAG, "Starting listener");
		phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// String stateString = "N/A";
				Log.v(TAG, "Starting CallStateChange");
				switch (state) {
				case TelephonyManager.CALL_STATE_OFFHOOK:
				case TelephonyManager.CALL_STATE_RINGING:
					if (mediaPlayer != null) {
						pauseMedia();
						isPausedInCall = true;
					}

					break;
				case TelephonyManager.CALL_STATE_IDLE:
					// Phone idle. Start playing.
					if (mediaPlayer != null) {
						if (isPausedInCall) {
							isPausedInCall = false;
							playMedia();
						}

					}
					break;
				}

			}
		};

		// Register the listener with the telephony manager
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Insert notification start
		initNotification();

		sntAudioLink = intent.getExtras().getString("sentAudioLink");
		mediaPlayer.reset();

		// Set up the MediaPlayer data source using the strAudioLink value
		if (!mediaPlayer.isPlaying()) {
			try {
//				mediaPlayer
//						.setDataSource(URL_STRING
//								+ sntAudioLink);
				if(sntAudioLink !=null){
					mediaPlayer
					.setDataSource(sntAudioLink);
				}else if(sntAudioLink == null){
					mediaPlayer
					.setDataSource("http://mahmud.vubon.com/audio/AL%20FATIHA.mp3");
				}
				
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

				// Send message to Activity to display progress dialogue
				sendBufferingBroadcast();
				// Prepare mediaplayer
				mediaPlayer.prepareAsync();

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
			}
		}
		// --- Set up seekbar handler ---
		setupHandler();

		return START_STICKY;
	}

	// ---Send seekbar info to activity----
	private void setupHandler() {
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
	}

	private Runnable sendUpdatesToUI = new Runnable() {
		@Override
		public void run() {
			// // Log.d(TAG, "entered sendUpdatesToUI");

			LogMediaPosition();

			handler.postDelayed(this, 1000); // 2 seconds

		}
	};

	private void LogMediaPosition() {
		// // Log.d(TAG, "entered LogMediaPosition");
		if (mediaPlayer.isPlaying()) {
			mediaPosition = mediaPlayer.getCurrentPosition();
			// if (mediaPosition < 1) {
			// Toast.makeText(this, "Buffering...", Toast.LENGTH_SHORT).show();
			// }
			mediaMax = mediaPlayer.getDuration();
			//seekIntent.putExtra("time", new Date().toLocaleString());
			seekIntent.putExtra("counter", String.valueOf(mediaPosition));
			seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
			seekIntent.putExtra("song_ended", String.valueOf(songEnded));
			sendBroadcast(seekIntent);
		}
	}

	// --Receive seekbar position if it has been changed by the user in the
	// activity
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateSeekPos(intent);
		}
	};

	// Update seek position from Activity
	public void updateSeekPos(Intent intent) {
		int seekPos = intent.getIntExtra("seekpos", 0);
		if (mediaPlayer.isPlaying()) {
			handler.removeCallbacks(sendUpdatesToUI);
			mediaPlayer.seekTo(seekPos);
			setupHandler();
		}

	}
	// ---End of seekbar code
	
	//broadcast for pause
	private BroadcastReceiver broadcastPause = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				pauseMedia();
				isPausedInCall = true;
			}else if (mediaPlayer != null && !mediaPlayer.isPlaying()){
				resumeMedia();
			}
		}
	};

	// If headset gets unplugged, stop music and service.
	private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
		private boolean headsetConnected = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// Log.v(TAG, "ACTION_HEADSET_PLUG Intent received");
			if (intent.hasExtra("state")) {
				if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
					headsetConnected = false;
					headsetSwitch = 0;
					// Log.v(TAG, "State =  Headset disconnected");
					// headsetDisconnected();
				} else if (!headsetConnected
						&& intent.getIntExtra("state", 0) == 1) {
					headsetConnected = true;
					headsetSwitch = 1;
					// Log.v(TAG, "State =  Headset connected");
				}

			}

			switch (headsetSwitch) {
			case (0):
				headsetDisconnected();
				break;
			case (1):
				break;
			}
		}

	};

	private void headsetDisconnected() {
		stopMedia();
		stopSelf();

	}

	
	// --- onDestroy, stop media player and release.  Also stop phoneStateListener, notification, receivers...---
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
		}

		if (phoneStateListener != null) {
			telephonyManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}

		// Cancel the notification
		cancelNotification();

		// Unregister headsetReceiver
		unregisterReceiver(headsetReceiver);

		// Unregister seekbar receiver
		unregisterReceiver(broadcastReceiver);
		
		//Unregister pause receiver
		unregisterReceiver(broadcastPause);

		// Stop the seekbar handler from sending updates to UI
		handler.removeCallbacks(sendUpdatesToUI);

		// Service ends, need to tell activity to display "Play" button
		resetButtonPlayStopBroadcast();
	}

	// Send a message to Activity that audio is being prepared and buffering
	// started.
	private void sendBufferingBroadcast() {
		// Log.v(TAG, "BufferStartedSent");
		bufferIntent.putExtra("buffering", "1");
		sendBroadcast(bufferIntent);
	}

	// Send a message to Activity that audio is prepared and ready to start
	// playing.
	private void sendBufferCompleteBroadcast() {
		// Log.v(TAG, "BufferCompleteSent");
		bufferIntent.putExtra("buffering", "0");
		sendBroadcast(bufferIntent);
	}

	// Send a message to Activity to reset the play button.
	private void resetButtonPlayStopBroadcast() {
		// Log.v(TAG, "BufferCompleteSent");
		bufferIntent.putExtra("buffering", "2");
		sendBroadcast(bufferIntent);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		
		if (!mediaPlayer.isPlaying()){
			playMedia();
			Toast.makeText(this,
					"SeekComplete", Toast.LENGTH_SHORT).show();
		}
		
	}

	
	//---Error processing ---
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
			sendBufferCompleteBroadcast();
			Toast.makeText(this,
					"MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra,
					Toast.LENGTH_SHORT).show();
			break;
		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
			sendBufferCompleteBroadcast();
			Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra,
					Toast.LENGTH_SHORT).show();
			break;
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:
			sendBufferCompleteBroadcast();
//			Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra,
//					Toast.LENGTH_LONG).show();
			Toast.makeText(this, "Unable to connect to the Internet " + extra,
					Toast.LENGTH_LONG).show();
			break;
		}
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {

		// Send a message to activity to end progress dialogue

		sendBufferCompleteBroadcast();
		playMedia();

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// When song ends, need to tell activity to display "Play" button
		stopMedia();
		stopSelf();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void playMedia() {
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
		}else if(mediaPlayer != null && !mediaPlayer.isPlaying() ){
			mediaPlayer.seekTo(lectureLength);
			mediaPlayer.start();
		}
	}

	// Add for Telephony Manager
	public void pauseMedia() {
		// Log.v(TAG, "Pause Media");
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			lectureLength=mediaPlayer.getCurrentPosition();
		}

	}
	
	public void resumeMedia(){
		mediaPlayer.seekTo(lectureLength);
		mediaPlayer.start();
	}

	public void stopMedia() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	// Create Notification
	@SuppressWarnings("deprecation")
	private void initNotification() {
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.calli;
		CharSequence tickerText = "Service started by Audio lecture collection";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		Context context = getApplicationContext();
		CharSequence contentTitle = "Audio playing in background";
		CharSequence contentText = "Authentic Islamic Lecture Collections";
		Intent notificationIntent = new Intent(this, LecturesList.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		Notification.Builder builder = new Notification.Builder(context)
				.setContentIntent(contentIntent)
				.setContentText(contentText)
				.setSmallIcon(R.drawable.calli)
				.setContentTitle(contentTitle);
		notification = builder.build();

//		notification.setLatestEventInfo(context, contentTitle, contentText,
//				contentIntent);

		mNotificationManager.notify(NOTIFICATION_ID, notification);
		
		// this
//		NotificationCompat.Builder mBuilder =
//		        new NotificationCompat.Builder(this)
//		        .setSmallIcon(R.drawable.ic_launcher)
//		        .setContentTitle("My notification")
//		        .setContentText("Hello World!");
//		// Creates an explicit intent for an Activity in your app
//		Intent resultIntent = new Intent(this, LecturesList.class);
//
//		// The stack builder object will contain an artificial back stack for the
//		// started Activity.
//		// This ensures that navigating backward from the Activity leads out of
//		// your application to the Home screen.
//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//		// Adds the back stack for the Intent (but not the Intent itself)
//		stackBuilder.addParentStack(LecturesList.class);
//		// Adds the Intent that starts the Activity to the top of the stack
//		stackBuilder.addNextIntent(resultIntent);
//		PendingIntent resultPendingIntent =
//		        stackBuilder.getPendingIntent(
//		            0,
//		            PendingIntent.FLAG_NO_CREATE
//		        );
//		mBuilder.setContentIntent(resultPendingIntent);
//		NotificationManager mNotificationManager =
//		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		// mId allows you to update the notification later on.
//		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
	}

	// Cancel Notification
	private void cancelNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
}

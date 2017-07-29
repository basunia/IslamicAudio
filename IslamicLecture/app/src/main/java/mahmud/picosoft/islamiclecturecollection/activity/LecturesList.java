package mahmud.picosoft.islamiclecturecollection.activity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import mahmud.picosoft.islamiclecturecollection.R;
import mahmud.picosoft.islamiclecturecollection.adapter.ActorAdapter;
import mahmud.picosoft.islamiclecturecollection.pojo.Actors;
import mahmud.picosoft.islamiclecturecollection.service.MyPlayService;
import mahmud.picosoft.islamiclecturecollection.tabfragment.ForegroundActivity;

public class LecturesList extends AppCompatActivity implements OnSeekBarChangeListener {
    int count = 0;
    String SAVE_NAME = "save_name";
    public static String filename = "MySharedStringData";
    SharedPreferences someData;
    // -----------------------------//
    boolean isPlay = false;
    String positionRow = null;
    String lecture = null;
    private static final String MUSIC_PLAYING = "Music_Playing";
    private static final String MUSIC_PAUSE = "Music_Pause";
    private SharedPreferences prefs = null;
    private String prefName = "MyPref";
    TextView wellcome;
    //InterstitialAd interstitial;

    // -------------------------------//
    ArrayList<Actors> actorsList;

    ActorAdapter adapter;

    // /////////------------
    Intent serviceIntent;
    private Button buttonPlay;
    private Button stopButton;
    private TextView tCurrentTime, tMaxTime, lectureName, buffer;

    // -- PUT THE NAME OF YOUR AUDIO FILE HERE...URL GOES IN THE SERVICE
    String strAudioLink = "10.mp3";

    private boolean isOnline;
    private boolean boolMusicPlaying = false;
    TelephonyManager telephonyManager;
    PhoneStateListener listener;

    // --Seekbar variables --
    private SeekBar seekBar;
    private int seekMax;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;

    String link = "http://ekushay.com/picosoft/auth_islamic_lecture/al_fatiha.mp3";
    String lectureTitle = "Select a lecture from the list";

    String downloadedLecturename = "Al Fatiha";

    // --Set up constant ID for broadcast of seekbar position--
    public static final String BROADCAST_SEEKBAR = "mahmud.picosoft.islamiclecturecollection.sendseekbar";
    Intent intent, pauseIntent;

    // Broadcast for pause media player
    public static final String BROADCAST_PAUSE = "mahmud.picosoft.islamiclecturecollection.pause";

    // Progress dialogue and broadcast receiver variables
    boolean mBufferBroadcastIsRegistered;
    private ProgressDialog pdBuff = null;

    // ----------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.media_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.icon2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        someData = getSharedPreferences(filename, 0);


        // another section
        try {
            serviceIntent = new Intent(this, MyPlayService.class);

            // --- set up seekbar intent for broadcasting new position to
            // service ---
            intent = new Intent(BROADCAST_SEEKBAR);
            pauseIntent = new Intent(BROADCAST_PAUSE);

            initViews();
            // lectureName.setText(lectureTitle);
            setListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        // get the link and name
        if (getIntent().getExtras() != null) {
            link = (String) getIntent().getExtras().get("positionRow");
            lectureTitle = (String) getIntent().getExtras().get("lecture");

            lectureName.setText(lectureTitle);
            wellcome.setText("\nNow Playing\n" + "" + lectureTitle);

            playAudio();

            // save the lecture title name
            SharedPreferences.Editor editor = someData.edit();
            editor.putString("sharedData", lectureTitle);
            editor.commit();

        }
        // setListeners();

        // ------------------------

    }

    // set lecture title
    void setText(String _lectureTitle) {
        lectureName.setText(_lectureTitle);
    }

    // -----------------
    // -- Broadcast Receiver to update position of seekbar from service --
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        String strSongEnded = serviceIntent.getStringExtra("song_ended");
        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        songEnded = Integer.parseInt(strSongEnded);
        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);

        // time
        // hh:mm:ss
        String time1 = String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(seekProgress),
                TimeUnit.MILLISECONDS.toMinutes(seekProgress)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(seekProgress)),
                TimeUnit.MILLISECONDS.toSeconds(seekProgress)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(seekProgress)));

        String time2 = String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(seekMax),
                TimeUnit.MILLISECONDS.toMinutes(seekMax)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(seekMax)),
                TimeUnit.MILLISECONDS.toSeconds(seekMax)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(seekMax)));

        tCurrentTime.setText(time1);
        tMaxTime.setText(time2);
        if (songEnded == 1) {
            buttonPlay.setBackgroundResource(R.drawable.play);
        }
    }

    // Display Interstitial Ad
    // public void displayInterstitial() {
    // if (interstitial.isLoaded()) {
    // interstitial.show();
    // }
    // }

    // --End of seekbar update code--

    // --- Set up initial screen ---
    private void initViews() {
        buttonPlay = (Button) findViewById(R.id.button1);
        // playButtonState();
        buttonPlay.setBackgroundResource(R.drawable.play);
        stopButton = (Button) findViewById(R.id.button2);
        stopButton.setBackgroundResource(R.drawable.stop);

        lectureName = (TextView) findViewById(R.id.textView3);
        // lectureName.setText(link);

        tCurrentTime = (TextView) findViewById(R.id.textView1);
        tMaxTime = (TextView) findViewById(R.id.textView2);

        // --Reference seekbar in main.xml
        seekBar = (SeekBar) findViewById(R.id.seekBar1);

        wellcome = (TextView) findViewById(R.id.wellcome);

        buffer = (TextView) findViewById(R.id.buffer);

    }

    private void playButtonState() {
        if (isPlay == false) {
            buttonPlay.setBackgroundResource(R.drawable.play);

        } else if (isPlay == true) {
            buttonPlay.setBackgroundResource(R.drawable.pause);

        }
    }

    // --- Set up listeners ---
    private void setListeners() {

        buttonPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // LecturesList.this.count++;
                // if (LecturesList.this.count == 2) {
                // LecturesList.this.displayInterstitial();
                // }

                buttonPlayStopClick();
            }
        });
        seekBar.setOnSeekBarChangeListener(this);
        stopButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // sendBroadcast(pauseIntent);
                stopMyPlayService();
                buffer.setText("");
                lectureName.setText("Select a lecture from lecture list");
                // save the lecture title name
                SharedPreferences.Editor editor = someData.edit();
                editor.putString("sharedData",
                        "Select a lecture from lecture list");
                editor.commit();

            }
        });

        wellcome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(getApplicationContext(),
                        ForegroundActivity.class));
                finish();
            }
        });

    }

    // --- invoked from ButtonPlayStop listener above ----
    // boolean isPlay = true;

    private void buttonPlayStopClick() {
        if (boolMusicPlaying == false) {
            buttonPlay.setBackgroundResource(R.drawable.pause);
            playAudio();
            boolMusicPlaying = true;
            isPlay = true;
        } else if (boolMusicPlaying == true) {
            if (isPlay == true) {
                buttonPlay.setBackgroundResource(R.drawable.play);
                isPlay = false;
            } else if (isPlay == false) {
                buttonPlay.setBackgroundResource(R.drawable.pause);
                isPlay = true;
            }

            // stopMyPlayService();
            sendBroadcast(pauseIntent);
            // boolMusicPlaying = false;
        }

    }

    // --- Stop service (and music) ---
    private boolean stopMyPlayService() {
        // --Unregister broadcastReceiver for seekbar
        if (mBroadcastIsRegistered) {
            try {
                unregisterReceiver(broadcastReceiver);
                mBroadcastIsRegistered = false;
            } catch (Exception e) {
                // Log.e(TAG, "Error in Activity", e);
                // TODO Auto-generated catch block

                e.printStackTrace();
                Toast.makeText(

                        getApplicationContext(),

                        e.getClass().getName() + " " + e.getMessage(),

                        Toast.LENGTH_LONG).show();
            }
        }

        try {
            stopService(serviceIntent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        return boolMusicPlaying = false;
    }

    // --- Start service and play music ---
    @SuppressWarnings("deprecation")
    private void playAudio() {

        checkConnectivity();
        if (isOnline) {
            stopMyPlayService();

            serviceIntent.putExtra("sentAudioLink", link);

            try {
                startService(serviceIntent);
            } catch (Exception e) {

                e.printStackTrace();
                Toast.makeText(

                        getApplicationContext(),

                        e.getClass().getName() + " " + e.getMessage(),

                        Toast.LENGTH_LONG).show();
            }

            // Displaying lecture title
            // lectureName.setText(lectureTitle);

            // -- Register receiver for seekbar--
            registerReceiver(broadcastReceiver, new IntentFilter(
                    MyPlayService.BROADCAST_ACTION));
            ;
            mBroadcastIsRegistered = true;

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Network Not Connected...");
            alertDialog.setMessage("Please connect to a network and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                }
            });
            alertDialog.setIcon(R.drawable.calli_);
            buttonPlay.setBackgroundResource(R.drawable.play);
            alertDialog.show();
        }
    }

    // ----------------------------------------------------------2355

    @Override
    protected void onDestroy() {

        // // /////////13 Feb edited by hasan
        // if (mBroadcastIsRegistered) {
        // unregisterReceiver(broadcastReceiver);
        // mBroadcastIsRegistered = false;
        // }

        super.onDestroy();
        // stopMyPlayService();
    }

    @Override
    protected void onRestart() {
        // // Register broadcast receiver
        // if (!mBufferBroadcastIsRegistered) {
        // registerReceiver(broadcastBufferReceiver, new IntentFilter(
        // MyPlayService.BROADCAST_BUFFER));
        // mBufferBroadcastIsRegistered = true;
        // }
        //
        // // -- Register receiver for seekbar--
        // if (!mBroadcastIsRegistered) {
        // registerReceiver(broadcastReceiver, new IntentFilter( // /////////13
        // // Feb
        // // edited by
        // // hasan
        // MyPlayService.BROADCAST_ACTION));
        // ;
        // mBroadcastIsRegistered = true;
        // }
        super.onRestart();
    }

    // Handle progress dialogue for buffering...
    private void showPD(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);

        // When the broadcasted "buffering" value is 1, show "Buffering"
        // progress dialogue.
        // When the broadcasted "buffering" value is 0, dismiss the progress
        // dialogue.

        switch (bufferIntValue) {
            case 0:
                // Log.v(TAG, "BufferIntValue=0 RemoveBufferDialogue");
                // txtBuffer.setText("");
                // edited
                // if (pdBuff != null) {
                // pdBuff.dismiss();
                // pdBuff = null;
                // }
                buffer.setText("");

                break;

            case 1:
                // BufferDialogue();
                bufferText();
                break;

            // Listen for "2" to reset the button to a play button
            case 2:
                buttonPlay.setBackgroundResource(R.drawable.play);
                stopMyPlayService();
                buffer.setText("");
                break;

        }
    }

    private void bufferText() {
        buffer.setText("Buffering...");
    }

    // Progress dialogue...
    private void BufferDialogue() {

        pdBuff = ProgressDialog.show(LecturesList.this, "Buffering...",
                "Acquiring lecture...", true);
    }

    // Set up broadcast receiver
    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };

    private void checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting()
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting())
            isOnline = true;
        else
            isOnline = false;
    }

    // -- onPause, unregister broadcast receiver. To improve, also save screen
    // data ---
    @Override
    protected void onPause() {

        // if buffering for long time, then dismiss it pressing back button
        // if (pdBuff != null) {
        // //stopMyPlayService();
        // finish();
        // pdBuff.dismiss();
        // pdBuff = null;
        //
        // }

        if (boolMusicPlaying == true) {
            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(MUSIC_PLAYING, boolMusicPlaying);
            editor.putBoolean(MUSIC_PAUSE, isPlay);
            // editor.putString(SAVE_NAME, lectureTitle);
            editor.commit();
        }

        // Unregister broadcast receiver
        if (mBufferBroadcastIsRegistered) {
            unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
        }

        if (mBroadcastIsRegistered) { // /////////13 Feb edited by hasan
            unregisterReceiver(broadcastReceiver);

            mBroadcastIsRegistered = false;
        }

        // download
        // unregisterReceiver(receiver);

        super.onPause();

    }

    // -- onResume register broadcast receiver. To improve, retrieve saved
    // screen data ---
    @Override
    protected void onResume() {
        // retreive the data from sharedPreferene
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        if (prefs != null) {
            boolMusicPlaying = prefs.getBoolean(MUSIC_PLAYING, false);
            isPlay = prefs.getBoolean(MUSIC_PAUSE, false);
            // lectureTitle = prefs.getString(SAVE_NAME, "Mahmud Basunia");
            // lectureName.setText(lectureTitle);
            // if(boolMusicPlaying == true){
            //
            // stopMyPlayService();
            // boolMusicPlaying = false;
            // }
            if (boolMusicPlaying == true) {
                someData = getSharedPreferences(filename, 0);
                String dataReturned = someData.getString("sharedData",
                        "couldn't load data");
                setText(dataReturned);
            }

            // lectureName.setText(dataReturned);
            playButtonState();
        }

        // Register broadcast receiver
        if (!mBufferBroadcastIsRegistered) {
            registerReceiver(broadcastBufferReceiver, new IntentFilter(
                    MyPlayService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }

        // -- Register receiver for seekbar--
        if (!mBroadcastIsRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter( // /////////13
                    // Feb
                    // edited by
                    // hasan
                    MyPlayService.BROADCAST_ACTION));
            mBroadcastIsRegistered = true;
        }

        // download
        // registerReceiver(receiver, new IntentFilter(
        // DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        super.onResume();
    }

    // --- When user manually moves seekbar, broadcast new position to service
    // ---
    @Override
    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        if (fromUser) {
            int seekPos = sb.getProgress();
            intent.putExtra("seekpos", seekPos);
            sendBroadcast(intent);
        }
    }

    // --- The following two methods are alternatives to track seekbar if moved.
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    // //-----------------

    // android menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.lecture:
                Intent i = new Intent(LecturesList.this, ForegroundActivity.class);
                startActivity(i);
                // boolMusicPlaying = false;
                // isPlay = false;
                finish();
                break;

            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent
                        .putExtra(
                                Intent.EXTRA_TEXT,
                                "Awesome Islamic Authentic Lecture Collection. Download it now http://goo.gl/YNeLhY");
                startActivity(Intent.createChooser(shareIntent, "Share this app.."));
                break;

            case R.id.rate:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=mahmud.picosoft.islamiclecturecollection"));
                startActivity(intent);
                break;
            case R.id.more:
                startActivity(new Intent(LecturesList.this, AboutActivity.class));
                /*Intent browserIntent2 = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/dev?id=9183820597138392476"));
                startActivity(browserIntent2);*/
                break;

            case R.id.download:
                new AlertDialog.Builder(this)
                        .setTitle("Download")
                        .setMessage("Do you want to download this lecture?")
                        .setPositiveButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // continue with delete
                                    }
                                })
                        .setNegativeButton("Download",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // do nothing
                                        // /-------------------------------
                                        downloadedLecturename = lectureTitle;
                                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        Request request = new Request(Uri
                                                .parse(link));
                                        enqueue = dm.enqueue(request);
                                        receiver = new BroadcastReceiver() {
                                            @Override
                                            public void onReceive(Context context,
                                                                  Intent intent) {
                                                String action = intent.getAction();
                                                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE
                                                        .equals(action)) {
                                                    downloadId = intent
                                                            .getLongExtra(
                                                                    DownloadManager.EXTRA_DOWNLOAD_ID,
                                                                    0);
                                                    Query query = new Query();
                                                    query.setFilterById(enqueue);
                                                    Cursor c = dm.query(query);
                                                    if (c.moveToFirst()) {
                                                        int columnIndex = c
                                                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                                                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                                                .getInt(columnIndex)) {

                                                            Toast.makeText(
                                                                    getApplicationContext(),
                                                                    "Download complete",
                                                                    Toast.LENGTH_LONG)
                                                                    .show();

                                                        }
                                                    }
                                                }
                                            }
                                        };

                                        registerReceiver(
                                                receiver,
                                                new IntentFilter(
                                                        DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                                    }
                                }).setIcon(R.drawable.icon2).show();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    // ---------------code for lecture doawnload-----------------
    private long enqueue;
    private DownloadManager dm;
    BroadcastReceiver receiver;
    long downloadId;

}

package com.example.brucewayne.music_player;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.content.Intent.EXTRA_USER;

public class MainActivity extends AppCompatActivity
    implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener,
    View.OnClickListener {
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private TextView textTotal;
    private MediaPlayer mp;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<Song> songsList;
    private String[] STAR = {"*"};
    public ArrayList<String> songsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songsList = new ArrayList<>();
        songsName = new ArrayList<>();
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnForward.setOnClickListener(this);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnBackward.setOnClickListener(this);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(this);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnRepeat.setOnClickListener(this);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        btnShuffle.setOnClickListener(this);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        textTotal = (TextView) findViewById(R.id.textPlaylist);
        mp = new MediaPlayer();
        utils = new Utilities();
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
        ListAllSongs();
        final ListView gv = (ListView) findViewById(R.id.listView);
        for (int i = 0; i < songsList.size(); i++)
            songsName.add(songsList.get(i).getName());
        textTotal.setText(String.valueOf(songsList.size()));
        ArrayAdapter<String> da = new ArrayAdapter<String>
            (
                this,
                android.R.layout.simple_list_item_1, songsName
            );
        gv.setAdapter(da);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playSong(position);
                textTotal.setText(String.valueOf(position + 1) + "/" + String.valueOf(songsList.size
                    ()));
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isRepeat) {
            playSong(currentSongIndex);
        } else if (isShuffle) {
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else {
            if (currentSongIndex < (songsList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
        mp.seekTo(currentPosition);
        updateProgressBar();
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            songProgressBar.setProgress(progress);
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onClick(View v) {
        int currentPosition = mp.getCurrentPosition();
        switch (v.getId()) {
            case R.id.btnPlay:
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    if (mp != null) {
                        mp.start();
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }
                break;
            case R.id.btnForward:
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    mp.seekTo(mp.getDuration());
                }
                break;
            case R.id.btnBackward:
                if (currentPosition - seekBackwardTime >= 0) {
                    mp.seekTo(currentPosition - seekBackwardTime);
                } else {
                    mp.seekTo(0);
                }
                break;
            case R.id.btnNext:
                if (currentSongIndex < (songsList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    playSong(0);
                    currentSongIndex = 0;
                }
                break;
            case R.id.btnPrevious:
                if (currentSongIndex > 0) {
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                } else {
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }
                break;
            case R.id.btnRepeat:
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT)
                        .show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT)
                        .show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
                break;
            case R.id.btnShuffle:
                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT)
                        .show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                } else {
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT)
                        .show();
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.reset();
        mp.release();
        mp = null;
    }

    public void playSong(int songIndex) {
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).getPath());
            mp.prepare();
            mp.start();
            String songTitle = songsList.get(songIndex).getName();
            songTitleLabel.setText("Now Playing: "+ songTitle);
            btnPlay.setImageResource(R.drawable.btn_pause);
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ListAllSongs() {
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = getContentResolver().query(allsongsuri, STAR,
            selection, null, null);
        if (isSdPresent()) {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String songname = cursor
                            .getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        String fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                        songsList.add(new Song(songname, fullpath));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
    }

    public static boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(
            android.os.Environment.MEDIA_MOUNTED);
    }
}

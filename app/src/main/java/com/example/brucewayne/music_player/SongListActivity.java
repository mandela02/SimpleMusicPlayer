package com.example.brucewayne.music_player;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.EXTRA_USER;

public class SongListActivity extends AppCompatActivity {
    public ArrayList<Song> songsList;
    public ArrayList<String> songsName;
    private String[] STAR = {"*"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        songsList = new ArrayList<>();
        songsName = new ArrayList<>();
        ListAllSongs();
        final ListView gv = (ListView) findViewById(R.id.gridView1);
        for (int i = 0; i < songsList.size(); i++)
            songsName.add(songsList.get(i).getName());
        ArrayAdapter<String> da = new ArrayAdapter<String>
            (
                this,
                android.R.layout.simple_list_item_1, songsName
            );
        gv.setAdapter(da);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songIndex = position;
                Intent in = new Intent(getApplicationContext(),
                    MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("songIndex", songIndex);
                setResult(100, in);
                finish();
/*
                startActivity(MainActivity.getInstance(SongListActivity.this, position));
*/

            }
        });
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

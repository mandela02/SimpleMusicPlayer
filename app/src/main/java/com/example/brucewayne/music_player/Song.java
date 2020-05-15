package com.example.brucewayne.music_player;

/**
 * Created by Bruce Wayne on 5/16/2017.
 */
public class Song {
    private String mName;
    private String mPath;

    public Song(String name, String path) {
        mName = name;
        mPath = path;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }
}

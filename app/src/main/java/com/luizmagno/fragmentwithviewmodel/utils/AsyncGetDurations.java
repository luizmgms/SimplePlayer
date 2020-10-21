package com.luizmagno.fragmentwithviewmodel.utils;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetDurations extends AsyncTask {

    ArrayList<Music> listMusics;
    MusicAdapter musicAdapter;
    MediaPlayer mediaPlayer = new MediaPlayer();

    public AsyncGetDurations(ArrayList<Music> list, MusicAdapter musicAdapter) {
        this.listMusics = list;
        this.musicAdapter = musicAdapter;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        for (Music music: listMusics) {
            music.setDurationMusic(loadDuration(music.getAbsolutePathMusic()));
        }

        mediaPlayer.release();

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        musicAdapter.notifyDataSetChanged();
    }

    private long loadDuration(String path) {

        long duration = 0;

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

        return duration;
    }
}

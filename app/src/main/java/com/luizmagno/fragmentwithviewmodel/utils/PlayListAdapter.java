package com.luizmagno.fragmentwithviewmodel.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.luizmagno.fragmentwithviewmodel.R;

import java.util.ArrayList;

public class PlayListAdapter extends BaseAdapter {

    private final ArrayList<Music> listMusics;
    private final Activity activity;
    private int currentPlaying = 0;
    private boolean stopped = true;
    private boolean paused = false;

    public PlayListAdapter(ArrayList<Music> musics, Activity activity) {
        this.listMusics = musics;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return listMusics.size();
    }

    @Override
    public Object getItem(int i) {
        return listMusics.get(i);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = activity.getLayoutInflater()
                .inflate(R.layout.item_playlist, viewGroup, false);

        Music music = listMusics.get(i);

        TextView nameMusic = v.findViewById(R.id.nameMusicId);
        nameMusic.setText(music.getNameMusic());
        LottieAnimationView playingAnimation = v.findViewById(R.id.playingAnimationId);

        if (!stopped && currentPlaying == i) {
            nameMusic.setTextColor(Color.WHITE);
            playingAnimation.setAnimation(R.raw.playing);
            playingAnimation.playAnimation();
        }
        if (currentPlaying == i && paused) {
            playingAnimation.pauseAnimation();
        }

        return v;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    public void setCurrentPlaying(int index) {
        this.currentPlaying = index;
    }
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}

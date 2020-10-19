package com.luizmagno.fragmentwithviewmodel.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.luizmagno.fragmentwithviewmodel.R;
public class ItemPlayListViewHolder extends RecyclerView.ViewHolder {

    public TextView nameMusic;
    public ImageView iconMusicalNote;
    public LottieAnimationView playingAnimation;

    public ItemPlayListViewHolder(@NonNull View itemView) {
        super(itemView);
        nameMusic = itemView.findViewById(R.id.nameMusicId);
        playingAnimation = itemView.findViewById(R.id.playingAnimationId);
    }

}

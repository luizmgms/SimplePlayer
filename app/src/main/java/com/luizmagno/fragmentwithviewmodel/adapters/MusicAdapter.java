package com.luizmagno.fragmentwithviewmodel.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.models.Music;
import com.luizmagno.fragmentwithviewmodel.utils.Utilities;

import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.MainActivity.currentSongIndex;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.mp;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.notifyPlayListAdapter;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.pause;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playList;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playListAdapter;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playPlayList;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playSong;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.stop;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.videoView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ItemMusicViewHolder>  {

    private final ArrayList<Music> listMusics;
    private final Activity activity;

    //InnerClass
    public static class ItemMusicViewHolder extends RecyclerView.ViewHolder {

        TextView nameMusic, durationMusic;
        ImageView btn_add_playlist;

        public ItemMusicViewHolder(View view) {
            super(view);

            nameMusic = view.findViewById(R.id.titleMusicId);
            durationMusic = view.findViewById(R.id.durationId);
            btn_add_playlist = view.findViewById(R.id.add);

        }
    }

    public MusicAdapter(ArrayList<Music> musics, Activity activity) {
        this.listMusics = musics;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return listMusics.size();
    }

    @NonNull
    @Override
    public MusicAdapter.ItemMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_music, parent, false);


        return new MusicAdapter.ItemMusicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MusicAdapter.ItemMusicViewHolder holder, final int position) {

        holder.nameMusic.setText(listMusics.get(position).getNameMusic());

        long time = listMusics.get(position).getDurationMusic();
        Utilities utilities = new Utilities();
        holder.durationMusic.setText(utilities.milliSecondsToTimer(time));

        holder.btn_add_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, R.string.add_to_list, Toast.LENGTH_SHORT).show();
                playList.add(listMusics.get(position));
                playListAdapter.notifyDataSetChanged();
            }
        });

        holder.nameMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, R.string.add_to_list, Toast.LENGTH_SHORT).show();
                playList.add(listMusics.get(position));
                currentSongIndex = playList.size()-1;
                Toast.makeText(activity, R.string.playing, Toast.LENGTH_SHORT).show();
                playSong(currentSongIndex);

            }
        });
    }

}

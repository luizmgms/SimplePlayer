package com.luizmagno.fragmentwithviewmodel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luizmagno.fragmentwithviewmodel.MainActivity;
import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.models.Music;
import com.luizmagno.fragmentwithviewmodel.utils.Utilities;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ItemMusicViewHolder>  {

    private final ArrayList<Music> listMusics;
    private final MainActivity activity;

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

    public MusicAdapter(ArrayList<Music> musics, MainActivity activity) {
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
                activity.playList.add(listMusics.get(position));
                activity.playListAdapter.notifyDataSetChanged();

            }
        });

        holder.nameMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, R.string.add_to_list, Toast.LENGTH_SHORT).show();
                activity.playList.add(listMusics.get(position));
                activity.currentSongIndex = activity.playList.size() - 1;
                Toast.makeText(activity, R.string.playing, Toast.LENGTH_SHORT).show();
                activity.playSong(activity.currentSongIndex);

            }
        });
    }

}

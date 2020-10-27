package com.luizmagno.fragmentwithviewmodel.adapters;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.luizmagno.fragmentwithviewmodel.MainActivity;
import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.fragments.ViewPagerAlbumFragment;
import com.luizmagno.fragmentwithviewmodel.models.Album;
import com.luizmagno.fragmentwithviewmodel.models.Music;

import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.DIRECTORY_MUSICS;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.PAGE_VIEW;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.POSITION;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.getListMusics;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.getPathDirectoryMusicsFromShared;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemAlbumViewHolder> {

    private final ArrayList<Album> listAlbuns;
    private final MainActivity activity;
    Toolbar toolbar;


    public static class ItemAlbumViewHolder extends RecyclerView.ViewHolder {

        TextView titleAlbum, qntdMusics;
        ImageView capaAlbum, buttonPlayAlbum;

        public ItemAlbumViewHolder(View view) {
            super(view);

            titleAlbum = view.findViewById(R.id.titleAlbum);
            capaAlbum = view.findViewById(R.id.capa);
            qntdMusics = view.findViewById(R.id.qntMusicsId);
            buttonPlayAlbum = view.findViewById(R.id.buttomPlayAlbumId);


        }
    }

    public AlbumAdapter(ArrayList<Album> list, MainActivity activity) {
        this.listAlbuns = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ItemAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_album, parent, false);

        return new ItemAlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemAlbumViewHolder holder, final int position) {

        final Album album = listAlbuns.get(position);

        holder.titleAlbum.setText(album.getNameAlbum());
        if (album.getPathCapaAlbum().equals("")){
            holder.capaAlbum.setImageResource(R.drawable.sem_capa);
        } else {
            holder.capaAlbum.setImageURI(Uri.parse(album.getPathCapaAlbum()));
        }

        //toolbar
        toolbar = activity.findViewById(R.id.toolbar);

        holder.capaAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Fragment fragment = AlbumFragment.newInstance();
                Fragment fragment = ViewPagerAlbumFragment.newInstance(activity);
                Bundle bundle = new Bundle();

                String pathAlbums = getPathDirectoryMusicsFromShared(activity);

                bundle.putString(DIRECTORY_MUSICS, pathAlbums);
                bundle.putInt(POSITION, position);

                /*bundle.putString("pathAlbum", album.getPathAlbum());
                bundle.putString("titleAlbum", album.getNameAlbum());
                bundle.putString("capaAlbum", album.getPathCapaAlbum());
                bundle.putInt("qntMusics", album.getNumOfMusics());*/

                //Set Arguments
                fragment.setArguments(bundle);

                ((FragmentActivity)view.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(PAGE_VIEW)
                        .commit();

                toolbar.setTitle(album.getNameAlbum());

            }
        });

        String qnt = album.getNumOfMusics()  +" " +
                activity.getResources().getString(R.string.musics);
        holder.qntdMusics.setText(qnt);

        holder.buttonPlayAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Music> musics =  getListMusics(album.getPathAlbum());
                activity.currentSongIndex = activity.playList.size();
                activity.playList.addAll(musics);
                activity.playSong(activity.currentSongIndex);
                Toast.makeText(activity, R.string.playing, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAlbuns.size();
    }

}

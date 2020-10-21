package com.luizmagno.fragmentwithviewmodel.utils;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.ui.main.AlbumFragment;

import java.io.File;
import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.MainActivity.currentSongIndex;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playList;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playSong;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemAlbumViewHolder> {

    private ArrayList<Album> listAlbuns;
    private Activity activity;
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

    public AlbumAdapter(ArrayList<Album> list, Activity activity) {
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

                Fragment fragment = AlbumFragment.newInstance();
                Bundle bundle = new Bundle();

                bundle.putString("pathAlbum", album.getPathAlbum());
                bundle.putString("titleAlbum", album.getNameAlbum());
                bundle.putString("capaAlbum", album.getPathCapaAlbum());
                bundle.putInt("qntMusics", album.getNumOfMusics());

                //Set Arguments
                fragment.setArguments(bundle);

                //Set Transitions
                fragment.setExitTransition(new Explode());
                fragment.setEnterTransition(new Explode());

                ((FragmentActivity)view.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment).addToBackStack("album")
                        //.setReorderingAllowed(true)
                        .commit();

                toolbar.setTitle(album.getNameAlbum());

            }
        });

        String qnt = album.getNumOfMusics()+" Músicas";
        holder.qntdMusics.setText(qnt);

        holder.buttonPlayAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Music> musics =  getListMusicsOfAlgum(album.getPathAlbum());
                currentSongIndex = playList.size();
                playList.addAll(musics);
                playSong(currentSongIndex);
                Toast.makeText(activity, R.string.playing, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAlbuns.size();
    }

    private ArrayList<Music> getListMusicsOfAlgum(String path) {
        ArrayList<Music> list = new ArrayList<>();

        File pastaAlbum = new File(path);
        File[] listaMusicas = pastaAlbum.listFiles();

        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é música
            for (File music : listaMusicas) {
                String name = music.getName();
                if (name.endsWith(".mp3")) {
                    Music m = new Music();
                    m.setNameMusic(music.getName());
                    m.setAbsolutePathMusic(music.getAbsolutePath());
                    list.add(m);
                }
            }

        }

        return list;
    }
}

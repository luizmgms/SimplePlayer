package com.luizmagno.fragmentwithviewmodel.utils;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.ui.main.AlbumFragment;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemAlbumViewHolder> {

    private ArrayList<Album> listAlbuns;
    private Activity activity;
    Toolbar toolbar;


    public static class ItemAlbumViewHolder extends RecyclerView.ViewHolder {

        TextView titleAlbum, qntdMusics;
        ImageView capaAlbum;

        public ItemAlbumViewHolder(View view) {
            super(view);

            titleAlbum = view.findViewById(R.id.titleAlbum);
            capaAlbum = view.findViewById(R.id.capa);
            qntdMusics = view.findViewById(R.id.qntMusicsId);


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
                .inflate(R.layout.item_list, parent, false);

        return new ItemAlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemAlbumViewHolder holder, int position) {

        final Album album = listAlbuns.get(position);
        holder.titleAlbum.setText(album.getNameAlbum());
        if (album.getPathCapaAlbum().equals("")){
            holder.capaAlbum.setImageResource(R.drawable.sem_capa);
        } else {
            holder.capaAlbum.setImageURI(Uri.parse(album.getPathCapaAlbum()));
        }

        ViewCompat.setTransitionName(holder.capaAlbum, "transform");

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

                fragment.setArguments(bundle);

                ((FragmentActivity)view.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(holder.capaAlbum, "transform")
                        .replace(R.id.container, fragment).addToBackStack("album")
                        .commit();
                toolbar.setTitle(album.getNameAlbum());

            }
        });

        String qnt = album.getNumOfMusics()+" Músicas";
        holder.qntdMusics.setText(qnt);
    }

    @Override
    public int getItemCount() {
        return listAlbuns.size();
    }

}

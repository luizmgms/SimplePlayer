package com.luizmagno.fragmentwithviewmodel.utils;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.luizmagno.fragmentwithviewmodel.ui.main.AlbumFragment;

import java.util.ArrayList;

public class PagerViewAlbumAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Album> listAlbuns;

    public PagerViewAlbumAdapter(@NonNull FragmentManager fm, ArrayList<Album> list) {
        super(fm);
        this.listAlbuns = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Album album = listAlbuns.get(position);
        Fragment fragment = AlbumFragment.newInstance();
        Bundle bundle = new Bundle();

        bundle.putString("pathAlbum", album.getPathAlbum());
        bundle.putString("titleAlbum", album.getNameAlbum());
        bundle.putString("capaAlbum", album.getPathCapaAlbum());
        bundle.putInt("qntMusics", album.getNumOfMusics());

        //Set Arguments
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return listAlbuns.size();
    }
}

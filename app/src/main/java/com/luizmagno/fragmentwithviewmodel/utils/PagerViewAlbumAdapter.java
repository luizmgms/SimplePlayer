package com.luizmagno.fragmentwithviewmodel.utils;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.luizmagno.fragmentwithviewmodel.ui.main.AlbumFragment;

import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.CAPA_ALBUM;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.PATH_ALBUM;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.QNT_MUSICS;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.TITLE_ALBUM;

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

        bundle.putString(PATH_ALBUM, album.getPathAlbum());
        bundle.putString(TITLE_ALBUM, album.getNameAlbum());
        bundle.putString(CAPA_ALBUM, album.getPathCapaAlbum());
        bundle.putInt(QNT_MUSICS, album.getNumOfMusics());

        //Set Arguments
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return listAlbuns.size();
    }
}

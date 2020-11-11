package com.luizmagno.fragmentwithviewmodel.utils;

import com.luizmagno.fragmentwithviewmodel.models.Album;

import java.util.Comparator;

public class AlbumComparator implements Comparator<Album> {
    @Override
    public int compare(Album a1, Album a2) {
        return a1.getNameAlbum().compareTo(a2.getNameAlbum());
    }
}

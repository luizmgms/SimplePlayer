package com.luizmagno.fragmentwithviewmodel.utils;

import com.luizmagno.fragmentwithviewmodel.models.Music;

import java.util.Comparator;

public class MusicComparator implements Comparator<Music> {
    @Override
    public int compare(Music m1, Music m2) {
        return m1.getNameMusic().compareTo(m2.getNameMusic());
    }
}

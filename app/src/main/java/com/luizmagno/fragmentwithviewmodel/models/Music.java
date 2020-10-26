package com.luizmagno.fragmentwithviewmodel.models;

public class Music {
    private String nameMusic;
    private String absolutePathMusic;
    private long durationMusic;

    public Music() {
    }

    public String getNameMusic() {
        return nameMusic;
    }

    public void setNameMusic(String nameMusic) {
        this.nameMusic = nameMusic;
    }

    public String getAbsolutePathMusic() {
        return absolutePathMusic;
    }

    public void setAbsolutePathMusic(String absolutePathMusic) {
        this.absolutePathMusic = absolutePathMusic;
    }

    public long getDurationMusic() {
        return durationMusic;
    }

    public void setDurationMusic(long durationMusic) {
        this.durationMusic = durationMusic;
    }
}



package com.luizmagno.fragmentwithviewmodel.models;

public class Album {
    private String nameAlbum;
    private String pathCapaAlbum;
    private String pathAlbum;
    private int numOfMusics;

    public Album() {}

    public String getNameAlbum() {
        return nameAlbum;
    }

    public void setNameAlbum(String nameAlbum) {
        this.nameAlbum = nameAlbum;
    }

    public String getPathCapaAlbum() {
        return pathCapaAlbum;
    }

    public void setPathCapaAlbum(String pathCapaAlbum) {
        this.pathCapaAlbum = pathCapaAlbum;
    }

    public String getPathAlbum() {
        return pathAlbum;
    }

    public void setPathAlbum(String pathAlbum) {
        this.pathAlbum = pathAlbum;
    }

    public int getNumOfMusics() {
        return numOfMusics;
    }

    public void setNumOfMusics(int numOfMusics) {
        this.numOfMusics = numOfMusics;
    }
}

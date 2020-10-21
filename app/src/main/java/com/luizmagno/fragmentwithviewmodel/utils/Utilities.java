package com.luizmagno.fragmentwithviewmodel.utils;

import java.io.File;

public class Utilities {

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        double percentage;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return (int) percentage;
    }

    /**
     * Function to change progress to timer
     * returns current duration in milliseconds
     * */
    /*public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }*/

    public Album getAlbum(File dirAlbum) {

        Album album = new Album();

        album.setNameAlbum(dirAlbum.getName());
        album.setPathCapaAlbum(getPathCapa(dirAlbum.getAbsolutePath()));
        album.setPathAlbum(dirAlbum.getAbsolutePath());
        album.setNumOfMusics(getNumOfMusics(dirAlbum.getAbsolutePath()));

        return  album;
    }

    public String getPathCapa(String pathAlbum) {

        String pathCapa = "";
        File pastaAlbum = new File(pathAlbum);
        File[] listaMusicas = pastaAlbum.listFiles();

        int temCapa = -1;
        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é imagem
            for (int i = 0; i < listaMusicas.length; i++) {
                //Se no nome conter extensão .png, .jpg ou .jpeg, set a capa.
                if (listaMusicas[i].getName().endsWith(".png") ||
                        listaMusicas[i].getName().endsWith(".PNG") ||
                        listaMusicas[i].getName().endsWith(".jpeg") ||
                        listaMusicas[i].getName().endsWith(".JPEG") ||
                        listaMusicas[i].getName().endsWith(".jpg") ||
                        listaMusicas[i].getName().endsWith(".JPG")){
                    temCapa = i;
                }
            }

        }

        if (temCapa != -1){
            pathCapa = listaMusicas[temCapa].getAbsolutePath();
        }

        return pathCapa;
    }

    public int getNumOfMusics(String pathAlbum) {

        int cont = 0;

        File pastaAlbum = new File(pathAlbum);
        File[] listaMusicas = pastaAlbum.listFiles();

        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é imagem
            for (File listaMusica : listaMusicas) {
                //Se no nome conter extensão .mp3
                if (listaMusica.getName().endsWith(".mp3")) {
                    cont++;
                }
            }

        }

        return cont;
    }
}
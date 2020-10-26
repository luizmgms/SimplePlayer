package com.luizmagno.fragmentwithviewmodel.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.luizmagno.fragmentwithviewmodel.MainActivity;
import com.luizmagno.fragmentwithviewmodel.models.Album;
import com.luizmagno.fragmentwithviewmodel.models.Music;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class Utilities {

    public final static  String SHARED_PREFERENCES = "com.luizmagno.music.preferences";
    public final static String DIRECTORY_MUSICS = "directoryMusic";
    public final static String NO_EXISTS = "noExists";
    public final static String POSITION = "position";
    public final static String PATH_ALBUM = "pathAlbum";
    public final static String TITLE_ALBUM = "titleAlbum";
    public final static String CAPA_ALBUM = "capaAlbum";
    public final static String QNT_MUSICS = "qntMusics";
    public final static String ALBUM = "album";
    public final static String VIDEO = "video";
    public final static String PATH_VIDEO = "pathVideo";
    public final static String PAGE_VIEW = "pageView";

    public final static int PERMISSION_REQUEST_READ_CARD = 0;

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
                if (listaMusica.getName().endsWith(".mp3") ||
                        listaMusica.getName().endsWith(".mp4")) {
                    cont++;
                }
            }

        }

        return cont;
    }

    public static boolean checkPermissionOfReadCard(Context context) {

        // Check if the read permission has been granted
        // Permission is already available
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static String getPathDirectoryMusicsFromShared(Activity activity) {

        SharedPreferences sharedPreferences = Objects.requireNonNull(
                activity).getSharedPreferences(
                Utilities.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        return sharedPreferences.getString(Utilities.DIRECTORY_MUSICS, NO_EXISTS);
    }

    public static void putStringOnShared(Activity activity, String string) {

        SharedPreferences sharedPreferences = Objects.requireNonNull(
                activity).getSharedPreferences(
                SHARED_PREFERENCES, Context.MODE_PRIVATE
        );

        sharedPreferences.edit().putString(DIRECTORY_MUSICS, string).apply();
    }

    public static void requestReadCardPermission(Activity activity) {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Objects.requireNonNull(activity),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_CARD);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_CARD);
        }
    }

    public static void startMain(Activity activity, String extra) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra(DIRECTORY_MUSICS, extra);
        activity.startActivity(intent);
        activity.finish();
    }

    public static ArrayList<Album> getListAlbuns(String path) {

        ArrayList<Album> list = new ArrayList<>();

        File pastaMusicas = new File(path);
        File[] listaAlbuns = pastaMusicas.listFiles();

        if (listaAlbuns != null && listaAlbuns.length != 0) {
            for (File listaAlbum : listaAlbuns) {
                Album album = new Utilities().getAlbum(listaAlbum);
                list.add(album);
            }
        }

        return list;
    }

    public static ArrayList<Music> getListMusics(String path) {

        ArrayList<Music> list = new ArrayList<>();

        File pastaAlbum = new File(path);
        File[] listaMusicas = pastaAlbum.listFiles();

        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é música
            for (File music : listaMusicas) {
                String name = music.getName();
                if (name.endsWith(".mp3") || name.endsWith(".mp4")) {
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
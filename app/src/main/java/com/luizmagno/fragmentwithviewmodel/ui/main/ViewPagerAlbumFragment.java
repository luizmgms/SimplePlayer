package com.luizmagno.fragmentwithviewmodel.ui.main;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.utils.Album;
import com.luizmagno.fragmentwithviewmodel.utils.PagerViewAlbumAdapter;
import com.luizmagno.fragmentwithviewmodel.utils.ZoomOutPageTransformer;

import java.io.File;
import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.MainActivity.toolbar;

public class ViewPagerAlbumFragment extends Fragment {

    private ArrayList<Album> listAlbuns;

    public static ViewPagerAlbumFragment newInstance() {
        return new ViewPagerAlbumFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.view_pager_album_fragment,container, false);

        String pathAlbuns = getArguments().getString("directoryMusic");
        final int position = getArguments().getInt("position");

        listAlbuns = getListAlbuns(pathAlbuns);

        ViewPager viewPager = fragment.findViewById(R.id.viewPagerAlbumId);

        PagerAdapter pagerAdapter = new PagerViewAlbumAdapter(
                getActivity().getSupportFragmentManager(), listAlbuns);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                toolbar.setTitle(listAlbuns.get(position).getNameAlbum());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        return fragment;
    }

    private ArrayList<Album> getListAlbuns(String path) {
        ArrayList<Album> list = new ArrayList<>();

        File pastaMusicas = new File(path);
        File[] listaAlbuns = pastaMusicas.listFiles();

        if (listaAlbuns != null && listaAlbuns.length != 0) {
            for (File listaAlbum : listaAlbuns) {
                Album album = getAlbum(listaAlbum);
                list.add(album);
            }
        }

        return list;
    }

    private Album getAlbum(File dirAlbum) {

        Album album = new Album();

        album.setNameAlbum(dirAlbum.getName());
        album.setPathCapaAlbum(getPathCapa(dirAlbum.getAbsolutePath()));
        album.setPathAlbum(dirAlbum.getAbsolutePath());
        album.setNumOfMusics(getNumOfMusics(dirAlbum.getAbsolutePath()));

        return  album;
    }

    private String getPathCapa(String pathAlbum) {

        String pathCapa = "";
        File pastaAlbum = new File(pathAlbum);
        File[] listaMusicas = pastaAlbum.listFiles();

        int temCapa = -1;
        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é imagem
            for (int i = 0; i < listaMusicas.length; i++) {
                //Se no nome conter extensão .png, .jpg ou .jpeg, set a capa.
                if (listaMusicas[i].getName().endsWith(".png") ||
                        listaMusicas[i].getName().endsWith(".jpeg") ||
                        listaMusicas[i].getName().endsWith(".jpg")) {
                    temCapa = i;
                }
            }

        }

        if (temCapa != -1){
            pathCapa = listaMusicas[temCapa].getAbsolutePath();
        }

        return pathCapa;
    }

    private int getNumOfMusics(String pathAlbum) {

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

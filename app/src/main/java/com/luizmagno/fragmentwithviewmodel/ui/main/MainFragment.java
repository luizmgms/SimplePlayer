package com.luizmagno.fragmentwithviewmodel.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.SplashActivity;
import com.luizmagno.fragmentwithviewmodel.utils.Album;
import com.luizmagno.fragmentwithviewmodel.utils.AlbumAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AlbumAdapter adapterAlbum;
    private View fragment;
    private ArrayList<Album> listAlbuns;
    private SharedPreferences sharedPreferences;

    LinearLayout layoutBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Preferences
        sharedPreferences = getActivity().getSharedPreferences(
                "com.luizmagno.music.preferences", Context.MODE_PRIVATE);

        //Fragment e View's
        fragment = inflater.inflate(R.layout.main_fragment, container, false);
        layoutBottomSheet = Objects.requireNonNull(getActivity()).findViewById(R.id.layout_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        mRecyclerView = fragment.findViewById(R.id.reclycerViewId);

        //Pegar caminho do diretório de Álbuns
        Bundle bundle = getActivity().getIntent().getExtras();
        assert bundle != null;
        String pathMusics = bundle.getString("directoryMusic","noExists");

        listAlbuns = new ArrayList<>();

        //Pegar albuns e add na lista e verifica se existe algum
        if (!pathMusics.equals("noExists")) {
            //Existe, add na lista
            File pastaMusicas = new File(pathMusics);
            File[] listaAlbuns = pastaMusicas.listFiles();

            if (listaAlbuns != null && listaAlbuns.length != 0) {
                for (File listaAlbun : listaAlbuns) {
                    Album album = getAlbum(listaAlbun);
                    listAlbuns.add(album);
                }
            } else {
                showAlert();
            }

        } else {
            chooseDirectory();
        }


        //Quando Scrolling
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0){
                    //Scrolling up...
                    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                } else if (dy > 0) {
                    //Scrollind Down...
                    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }
            }
        });

        //Reclycer
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapterAlbum = new AlbumAdapter(listAlbuns, getActivity());
        mRecyclerView.setAdapter(adapterAlbum);
        mRecyclerView.setHasFixedSize(true);

        return fragment;
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

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Escolha a pasta dos Álbuns")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        chooseDirectory();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    private void chooseDirectory() {

        final StorageChooser chooser = new StorageChooser.Builder()
                // Specify context of the dialog
                .withActivity(getActivity())
                .withFragmentManager(getActivity().getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                // Define the mode as the FOLDER/DIRECTORY CHOOSER
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        // 2. Handle what should happend when the user selects the directory !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                // e.g /storage/emulated/0/Documents
                sharedPreferences.edit().putString("directoryMusic", path).apply();
                restartApp();
            }
        });

        // 3. Display File Picker whenever you need to !
        chooser.show();
    }

    private void restartApp() {
        Intent i = new Intent(getActivity(), SplashActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
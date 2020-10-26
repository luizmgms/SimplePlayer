package com.luizmagno.fragmentwithviewmodel.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.luizmagno.fragmentwithviewmodel.models.Album;
import com.luizmagno.fragmentwithviewmodel.adapters.AlbumAdapter;
import com.luizmagno.fragmentwithviewmodel.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.DIRECTORY_MUSICS;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.NO_EXISTS;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.putStringOnShared;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.startMain;

public class MainFragment extends Fragment {

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


        //Fragment e View's
        View fragment = inflater.inflate(R.layout.main_fragment, container, false);
        layoutBottomSheet = Objects.requireNonNull(getActivity()).findViewById(R.id.layout_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        RecyclerView mRecyclerView = fragment.findViewById(R.id.reclycerViewId);

        //Pegar caminho do diretório de Álbuns
        Bundle bundle = getActivity().getIntent().getExtras();
        assert bundle != null;
        String pathMusics = bundle.getString(DIRECTORY_MUSICS,NO_EXISTS);

        ArrayList<Album> listAlbuns = new ArrayList<>();

        //Pegar albuns e add na lista e verifica se existe algum
        if (!pathMusics.equals(NO_EXISTS)) {
            //Existe, add na lista
            File pastaMusicas = new File(pathMusics);
            File[] listaAlbuns = pastaMusicas.listFiles();

            if (listaAlbuns != null && listaAlbuns.length != 0) {
                for (File listaAlbum : listaAlbuns) {
                    Album album = new Utilities().getAlbum(listaAlbum);
                    listAlbuns.add(album);
                }
            } else {
                showAlert();
            }

        } else {
            chooseDirectory();
        }


        /*//Quando Scrolling
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
        });*/

        //Reclycer
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        AlbumAdapter adapterAlbum = new AlbumAdapter(listAlbuns, getActivity());
        mRecyclerView.setAdapter(adapterAlbum);
        mRecyclerView.setHasFixedSize(true);

        return fragment;
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setMessage(R.string.choose_dir)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        chooseDirectory();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                .withFragmentManager(Objects.requireNonNull(getActivity()).getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                // Define the mode as the FOLDER/DIRECTORY CHOOSER
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        // 2. Handle what should happend when the user selects the directory !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {

                putStringOnShared(getActivity(), path);
                startMain(getActivity(), path);
            }
        });

        // 3. Display File Picker whenever you need to !
        chooser.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
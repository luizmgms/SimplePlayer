package com.luizmagno.fragmentwithviewmodel.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luizmagno.fragmentwithviewmodel.MainActivity;
import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.adapters.MusicAdapter;
import com.luizmagno.fragmentwithviewmodel.models.Music;
import com.luizmagno.fragmentwithviewmodel.utils.AsyncGetDurations;
import com.luizmagno.fragmentwithviewmodel.utils.MusicComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.CAPA_ALBUM;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.PATH_ALBUM;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.QNT_MUSICS;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.TITLE_ALBUM;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.getListMusics;

public class AlbumFragment extends Fragment {

    private String pathAlbum;
    private String nameAlbum;
    private String pathCapa;
    private int quantMusics;
    private ArrayList<Music> listMusics;
    private MusicAdapter musicAdapter;
    private final MainActivity mainActivity;

    public AlbumFragment(MainActivity activity) {
        this.mainActivity = activity;
    }

    public static AlbumFragment newInstance(MainActivity activity) {
       return new AlbumFragment(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.album_fragment, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            pathAlbum = bundle.getString(PATH_ALBUM);
            nameAlbum = bundle.getString(TITLE_ALBUM);
            pathCapa = bundle.getString(CAPA_ALBUM);
            quantMusics = bundle.getInt(QNT_MUSICS);
        }

        ImageView capa = fragment.findViewById(R.id.capaAlbumId);
        TextView titleAlbum = fragment.findViewById(R.id.titleAlbumId);
        TextView qntMusicsAlbum = fragment.findViewById(R.id.qntMusicsAlbumId);
        RecyclerView listViewMusics = fragment.findViewById(R.id.listMusicsAlbumsId);
        //loadingDurations = fragment.findViewById(R.id.loadingDurationsId);

        if(!pathCapa.equals("")) {
            capa.setImageURI(Uri.parse(pathCapa));
        }

        capa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pathCapa.equals("")) {
                    Toast.makeText(getActivity(), R.string.no_cape, Toast.LENGTH_SHORT).show();
                } else {
                    openImage(pathCapa);
                }
            }
        });

        titleAlbum.setText(nameAlbum);
        String qnt = quantMusics + " " +
                Objects.requireNonNull(getActivity()).getResources().getString(R.string.musics);
        qntMusicsAlbum.setText(qnt);

        //Lista das Músicas
        listMusics = getListMusics(pathAlbum);

        //Ordenando..
        MusicComparator comp = new MusicComparator();
        Collections.sort(listMusics, comp);

        //Layout do ReclycerView
        listViewMusics.setLayoutManager(new LinearLayoutManager(getContext()));

        //Adapter da lista da Músicas
        musicAdapter = new MusicAdapter(listMusics, mainActivity);
        listViewMusics.setAdapter(musicAdapter);
        listViewMusics.setHasFixedSize(true);

        //Botão AddAll
        ImageView btnAddAll = fragment.findViewById(R.id.icAddAllId);
        btnAddAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.added_all_to_playlist,
                        Toast.LENGTH_SHORT).show();
                mainActivity.playList.addAll(listMusics);
                mainActivity.playListAdapter.notifyDataSetChanged();
            }
        });

        //Botão PlayAll
        ImageView btnPlayAll = fragment.findViewById(R.id.icPlayAllId);
        btnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.currentSongIndex = mainActivity.playList.size();
                mainActivity.playList.addAll(listMusics);
                Toast.makeText(getActivity(), R.string.playing, Toast.LENGTH_SHORT).show();
                mainActivity.playSong(mainActivity.currentSongIndex);
            }
        });

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        AsyncGetDurations asyncGetDurations = new AsyncGetDurations(listMusics, musicAdapter);
        asyncGetDurations.execute(new Object());

    }

    public void openImage(String imagePath) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(imagePath), "image/*");
        startActivity(intent);
    }

}
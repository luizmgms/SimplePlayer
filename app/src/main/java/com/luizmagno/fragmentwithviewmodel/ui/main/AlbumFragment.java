package com.luizmagno.fragmentwithviewmodel.ui.main;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.utils.Music;
import com.luizmagno.fragmentwithviewmodel.utils.MusicAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.MainActivity.currentSongIndex;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playList;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playListAdapter;
import static com.luizmagno.fragmentwithviewmodel.MainActivity.playSong;

public class AlbumFragment extends Fragment {

    private View fragment;
    private String pathAlbum;
    private String nameAlbum;
    private String pathCapa;
    private int quantMusics;
    private ImageView capa;
    private TextView titleAlbum, qntMusicsAlbum;
    private RecyclerView listViewMusics;
    private ArrayList<Music> listMusics;
    private MusicAdapter musicAdapter;
    private ImageView btnAddAll, btnPlayAll;
    private ProgressBar loadingDurations;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance() {
       return new AlbumFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragment = inflater.inflate(R.layout.album_fragment, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            pathAlbum = bundle.getString("pathAlbum");
            nameAlbum = bundle.getString("titleAlbum");
            pathCapa = bundle.getString("capaAlbum");
            quantMusics = bundle.getInt("qntMusics");
        }

        capa = fragment.findViewById(R.id.capaAlbumId);
        titleAlbum = fragment.findViewById(R.id.titleAlbumId);
        qntMusicsAlbum = fragment.findViewById(R.id.qntMusicsAlbumId);
        listViewMusics = fragment.findViewById(R.id.listMusicsAlbumsId);
        loadingDurations = fragment.findViewById(R.id.loadingDurationsId);

        if(!pathCapa.equals("")) {
            capa.setImageURI(Uri.parse(pathCapa));
        }

        titleAlbum.setText(nameAlbum);
        String qnt = quantMusics + " Músicas";
        qntMusicsAlbum.setText(qnt);

        //Lista das Músicas
        listMusics = new ArrayList<>();
        //Preenchendo Lista das Músicas
        fillListMusics();

        //Layout do ReclycerView
        listViewMusics.setLayoutManager(new LinearLayoutManager(getContext()));

        //Adapter da lista da Músicas
        musicAdapter = new MusicAdapter(listMusics, getActivity());
        listViewMusics.setAdapter(musicAdapter);
        listViewMusics.setHasFixedSize(true);

        //Botão AddAll
        btnAddAll = fragment.findViewById(R.id.icAddAllId);
        btnAddAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.added_all_to_playlist,
                        Toast.LENGTH_SHORT).show();
                playList.addAll(listMusics);
                playListAdapter.notifyDataSetChanged();
            }
        });

        //Botão PlayAll
        btnPlayAll = fragment.findViewById(R.id.icPlayAllId);
        btnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSongIndex = playList.size();
                playList.addAll(listMusics);
                Toast.makeText(getActivity(), R.string.playing, Toast.LENGTH_SHORT).show();
                playSong(currentSongIndex);
            }
        });


        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mediaPlayer = new MediaPlayer();
                for (Music music: listMusics) {
                    music.setDurationMusic(loadDuration(music.getAbsolutePathMusic(), mediaPlayer));
                }
                mediaPlayer.release();

                musicAdapter.notifyDataSetChanged();

                loadingDurations.setVisibility(View.INVISIBLE);
            }
        }, 0);
    }

    private void fillListMusics() {

        MediaPlayer mediaPlayer = new MediaPlayer();

        File pastaAlbum = new File(pathAlbum);
        File[] listaMusicas = pastaAlbum.listFiles();

        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é música
            for (File music : listaMusicas) {
                String name = music.getName();
                if (name.endsWith(".mp3")) {
                    Music m = new Music();
                    m.setNameMusic(music.getName());
                    m.setAbsolutePathMusic(music.getAbsolutePath());
                    //m.setDurationMusic(loadDuration(music.getAbsolutePath(), mediaPlayer));
                    listMusics.add(m);
                }
            }

        }

        mediaPlayer.release();
    }

    private long loadDuration(String path, MediaPlayer mediaPlayer) {

        long duration = 0;

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

        return duration;
    }
}
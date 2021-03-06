package com.luizmagno.fragmentwithviewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luizmagno.fragmentwithviewmodel.adapters.PlayListAdapter;
import com.luizmagno.fragmentwithviewmodel.fragments.MainFragment;
import com.luizmagno.fragmentwithviewmodel.models.Music;
import com.luizmagno.fragmentwithviewmodel.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.putStringOnShared;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.startMain;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView buttonExpandBottomSheet, buttonClosePlayer;
    private LinearLayout layoutBottomSheet;
    @SuppressWarnings("rawtypes")
    public BottomSheetBehavior bottomSheetBehavior;
    private TextView nameMusicCurrent;
    private ImageView iconMusicCurrent;
    public ArrayList<Music> playList;
    public PlayListAdapter playListAdapter;

    public boolean stop = true;
    public boolean pause = false;
    public int currentSongIndex = 0;
    //private ProgressBar progressBar;
    AppCompatSeekBar progressBar;
    private final Handler mHandler = new Handler();
    private final Handler handleHideBottomSheet = new Handler();
    private final Utilities utils = new Utilities();
    private TextView timeCurrent;
    private TextView timeTotal;

    private FloatingActionButton buttonPlay;

    public MediaPlayer mp = new MediaPlayer();

    public VideoView videoView;
    public boolean playingVideo = false;
    private ConstraintLayout layoutVideo;
    private AppBarLayout appBarLayout;
    private boolean videoHide = false;
    private ImageView btnHideShowVideo;
    public boolean fullscreen = false;
    private ImageView btnFullscreen;
    private CoordinatorLayout mCoordinatorLayout;
    private View barExpColHidBottomSheet;
    private int currentPositionVideo = 0;

    public View decorView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //video Example
        String uriRawVideoExample = "android.resource://" + getPackageName() + "/" + R.raw.example;

        //LayoutMain
        mCoordinatorLayout = findViewById(R.id.coordinator);

        //Decorations
        decorView = getWindow().getDecorView();

        //WakeOn
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Fragment
        Fragment fragment = MainFragment.newInstance(this);
        fragment.setExitTransition(new Slide());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        //Botão para expandir ou collapse BottomSheet
        buttonExpandBottomSheet = findViewById(R.id.button_expand_bottom_sheet);
        //Click do botão
        buttonExpandBottomSheet.setOnClickListener(listenerButtonExpand());

        //Barra de Expandir, Colapsar e Esconder BottomSheet
        barExpColHidBottomSheet = findViewById(R.id.barExpandColapsHideBottomSheetId);

        //Botão para ocultar Player
        buttonClosePlayer = findViewById(R.id.buttonClosePlayerId);
        //click do botão;
        buttonClosePlayer.setOnClickListener(listenerButtonClose());

        //Layout do BottomShet
        layoutBottomSheet = findViewById(R.id.layout_bottom_sheet);

        //Comportamento do BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        //Callback da mudança de estados
        bottomSheetBehavior.addBottomSheetCallback(callbackSheetBehavior());

        //Layout do Player
        ConstraintLayout layoutPlayer = findViewById(R.id.layoutPlayerId);

        //Pega altura do Player
        getHeightOfLayoutView(layoutPlayer);
        //Set bottomSheet como fullscreen
        setFullScreenBottomSheet();

        //Nome da música tocando
        nameMusicCurrent = findViewById(R.id.nameMusicIsPlayingId);
        nameMusicCurrent.setSelected(true);

        //Icone da Música tocando
        iconMusicCurrent = findViewById(R.id.iconMusicCurrentId);

        //Times
        timeCurrent = findViewById(R.id.currentTimeId);
        timeTotal = findViewById(R.id.durationTimeId);

        //ProgressBar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!stop) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    if (playingVideo) {
                        long total = utils.progressToTimer(progress, videoView.getDuration());
                        String strTime = utils.milliSecondsToTimer(total);
                        timeCurrent.setText(strTime);
                    } else {
                        long total = utils.progressToTimer(progress, mp.getDuration());
                        String strTime = utils.milliSecondsToTimer(total);
                        timeCurrent.setText(strTime);
                    }
                } else {
                    seekBar.setProgress(0);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!stop) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    int totalDuration;
                    if (playingVideo) {
                        totalDuration = videoView.getDuration();
                    } else {
                        totalDuration = mp.getDuration();
                    }

                    int position = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                    // forward or backward to certain seconds
                    if (playingVideo) {
                        videoView.seekTo(position);
                    } else {
                        mp.seekTo(position);
                    }

                    // update timer progress again
                    updateProgressBar();
                }
            }
        });

        //PlayList
        final ListView playListView = findViewById(R.id.playListId);

        //Inicializando PlayList
        playList = new ArrayList<>();

        //PlayListAdapter
        playListAdapter = new PlayListAdapter(playList, this);
        playListView.setAdapter(playListAdapter);

        //ClickListener Playlist
        playListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentSongIndex = i;
                playSong(i);
            }
        });

        //Set Listener para Captura de Scroll para PlayList
        playListView.setOnTouchListener(onTouchListener());

        //botão PlayPause
        buttonPlay = findViewById(R.id.fabPlayId);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPlayList();
            }
        });

        //botão Next
        FloatingActionButton buttonNext = findViewById(R.id.fabNextId);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMusic();
            }
        });

        //botão Previous
        FloatingActionButton buttonPrevious = findViewById(R.id.fabPreviousId);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMusic();
            }
        });

        //botão ClearAll
        ImageView buttonClearAll = findViewById(R.id.icClearAllId);
        buttonClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playList.clear();
                currentSongIndex = 0;
                playListAdapter.notifyDataSetChanged();
            }
        });

        //OnCompletionListenerMusic
        mp.setOnCompletionListener(onCompletionListener());

        //LayoutVideo
        layoutVideo = findViewById(R.id.layoutVideoId);
        layoutVideo.setOnTouchListener(onTouchVideoListener());

        //VideoView
        videoView = findViewById(R.id.videoViewId);

        /* BUG:
        * A primeira vez que vai tocar um vídeo ele lança uma exceção e chama
        * o onCompletionListener, mas toca o vídeo.
        * Por isso as duas linhas abaixo de código servem para, não corrigir, mas
        * burlar esse BUG. Ele toca uma video de exemplo no onCreate.
        * */
        videoView.setVideoURI(Uri.parse(uriRawVideoExample));
        videoView.start();

        //OnCompletionListenerVideo
        videoView.setOnCompletionListener(onCompletionVideoListener());

        //AppBarLayout
        appBarLayout = findViewById(R.id.app_bar_layout);

        //Botão Esconder/Mostrar Video
        btnHideShowVideo = findViewById(R.id.btnHideShowVideoId);
        btnHideShowVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideShowVideo();
            }
        });

        //Botão FullScreenMode
        btnFullscreen = findViewById(R.id.buttonFullscreenId);
        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullscreenMode();
            }
        });


    }

    /****************
    *
    *   MEUS METÓDOS
    *
    * ***************/

    private void hideShowVideo() {
        if (videoHide) {
            //Se o video está escondido...
            //Aumenta sua elevação para mostrar
            ViewCompat.setElevation(layoutVideo, 8);
            //Muda imagem do botão para video off
            btnHideShowVideo.setImageResource(R.drawable.ic_video_off);
            //Botão fullscreen fica visivel
            btnFullscreen.setVisibility(View.VISIBLE);
            //set videoHide para false
            videoHide = false;
        } else {
            //Video não está escondido...
            //Baixa sua elevação para esconder
            ViewCompat.setElevation(layoutVideo, -8);
            //Muda imagem do botão para video on
            btnHideShowVideo.setImageResource(R.drawable.ic_video_on);
            //Botão fullscreen fica invisivel
            btnFullscreen.setVisibility(View.INVISIBLE);
            //set videoHide para true
            videoHide = true;
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getNavBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier(
                "navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void fullscreenMode() {

            if (fullscreen && playingVideo) {
                //Está em tela cheia e tocando Video
                //Modo fullscreen vai para false
                fullscreen = false;
                //Set Image de botões e barra
                //Image Fullscreen
                btnFullscreen.setImageResource(R.drawable.ic_fullscreen);
                //Fica Visivel
                btnHideShowVideo.setVisibility(View.VISIBLE);
                barExpColHidBottomSheet.setVisibility(View.VISIBLE);
                //Orientação vai para retrato
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //Volta decorations
                int uiOptions =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                postDelayedDimensions();
                //Mostra BottomSheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


            } else if (playingVideo && !videoHide) {
                //Não está em tela cheia, mas está tocando video e tela de video não está escondida
                //Fullscreen vai para true
                fullscreen = true;
                //SetImage de botões e barra
                //Vai para fullscreen exit
                btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
                //Fica Invisivel
                btnHideShowVideo.setVisibility(View.INVISIBLE);
                barExpColHidBottomSheet.setVisibility(View.INVISIBLE);
                //Orientação vai para Paisagem
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
                //Esconde decorations
                int uiOptions =
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                //Redefine dimensões
                postDelayedDimensions();
                //Esconde BottomSheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            } else if (fullscreen) {
                //está no modo fullscreen, mas não tem video tocando
                fullscreen = false;
                //Set Image de botões
                //botão Fullscreen fica invisivel
                btnFullscreen.setVisibility(View.INVISIBLE);
                //botão hideVideo Fica invisivel
                btnHideShowVideo.setVisibility(View.INVISIBLE);
                //Barra fica visivel
                barExpColHidBottomSheet.setVisibility(View.VISIBLE);
                //Orientação vai para retrato
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //Volta decorations
                int uiOptions =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                postDelayedDimensions();
                //Mostra BottomSheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }

    }

    private void postDelayedDimensions() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ViewGroup.LayoutParams childLayoutParams = layoutVideo.getLayoutParams();

                childLayoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;

                int orientation = getResources().getConfiguration().orientation;

                ViewGroup.MarginLayoutParams cLayParmsCoo =
                        (ViewGroup.MarginLayoutParams) mCoordinatorLayout.getLayoutParams();

                if (orientation == Configuration.ORIENTATION_PORTRAIT) {

                    cLayParmsCoo.setMargins(0, getStatusBarHeight(), 0, getNavBarHeight());
                    mCoordinatorLayout.setLayoutParams(cLayParmsCoo);

                    childLayoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                } else {

                    cLayParmsCoo.setMargins(0, 0, 0, 0);
                    mCoordinatorLayout.setLayoutParams(cLayParmsCoo);

                    childLayoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
                }

                layoutVideo.setLayoutParams(childLayoutParams);
            }
        }, 500);
    }

    private MediaPlayer.OnCompletionListener onCompletionVideoListener () {
        return (new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                videoView.stopPlayback();
                layoutVideo.setVisibility(View.INVISIBLE);
                btnHideShowVideo.setVisibility(View.INVISIBLE);
                btnFullscreen.setVisibility(View.INVISIBLE);

                playingVideo = false;

                if (!playList.isEmpty()) {
                    //Se a PlayList não estiver vazia
                    if (currentSongIndex == playList.size() - 1) {
                        //Música que está tocando é a última da lista, então parar de tocar
                        if(fullscreen) {
                            fullscreenMode();
                        }
                        setStoppedAtribsUI();
                    } else {
                        //Não é a última então toque a próxima música
                        mp.stop();
                        stop = true;
                        pause = false;
                        currentSongIndex++;
                        notifyPlayListAdapter();
                        playPlayList();
                    }

                } else {
                    //Lista está vazia
                    if (fullscreen){
                        fullscreenMode();
                    }
                    setStoppedAtribsUI();
                }
            }
        });
    }

    private MediaPlayer.OnCompletionListener onCompletionListener() {
        return (new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if (!playList.isEmpty()) {
                    //Se a PlayList não estiver vazia
                    if (currentSongIndex == playList.size() - 1) {
                        //Música que está tocando é a última da lista, então parar de tocar
                        setStoppedAtribsUI();
                    } else {
                        //Não é a última então toque a próxima música
                        mp.stop();
                        stop = true;
                        pause = false;
                        currentSongIndex++;
                        notifyPlayListAdapter();
                        playPlayList();
                    }

                } else {
                    //Lista está vazia
                    setStoppedAtribsUI();
                }

            }
        });
    }

    /**
     * Update timer on progressBar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            long totalDuration;
            long currentDuration;

            if (playingVideo) {
                totalDuration = videoView.getDuration();
                currentDuration = videoView.getCurrentPosition();
            } else {
                totalDuration = mp.getDuration();
                currentDuration = mp.getCurrentPosition();
            }


            // Displaying Total Duration time ans time completed
            String total = utils.milliSecondsToTimer(totalDuration);
            String atual = utils.milliSecondsToTimer(currentDuration);
            timeCurrent.setText(atual);
            timeTotal.setText(total);

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            progressBar.setProgress(progress);

            // Running this thread after 50 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    private void previousMusic () {
        if (playList.isEmpty()) {
            //Se a playList está vazia...
            if (mp.isPlaying() || playingVideo || pause) {
                //Mas está tocando ou em pausa...
                Toast.makeText(this, R.string.list_empty, Toast.LENGTH_SHORT).show();
            } else {
                //Não está tocando nem está em pausa
                Toast.makeText(this, R.string.list_empty, Toast.LENGTH_SHORT).show();
                setStoppedAtribsUI();
            }

        } else {
            //A playList não está vazia
            if (currentSongIndex == 0) {
                //Se for a primeira...
                currentSongIndex = playList.size() - 1;
            } else {
                //Se não...
                currentSongIndex--;
            }

            playSong(currentSongIndex);

        }
    }

    private void nextMusic() {
        if (playList.isEmpty()) {
            //Se a playList está vazia
            if (mp.isPlaying() || playingVideo || pause) {
                //...Mas tá tocando ou está em pausa
                Toast.makeText(this, R.string.list_empty, Toast.LENGTH_SHORT).show();
            } else {
                //...Não está tocando e nem está em pausa
                Toast.makeText(this, R.string.list_empty, Toast.LENGTH_SHORT).show();
                setStoppedAtribsUI();
            }

        } else {
            //A playList não está vazia
            if (currentSongIndex == playList.size() - 1) {
                //se for a última...
                currentSongIndex = 0;
            } else {
                //Se não...
                currentSongIndex++;
            }

            playSong(currentSongIndex);
        }
    }

    private ListView.OnTouchListener onTouchListener() {
        return new ListView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow NestedScrollView to intercept touch events.
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allow NestedScrollView to intercept touch events.
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                view.onTouchEvent(motionEvent);

                return true;
            }
        };
    }

    private final Runnable runHideBottomSheet = new Runnable() {
        @Override
        public void run() {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    };

    private View.OnTouchListener onTouchVideoListener() {
        return (new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getAction();

                if (fullscreen && playingVideo) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        // Disallow NestedScrollView to intercept touch events.
                        view.getParent().requestDisallowInterceptTouchEvent(true);

                    } else if (action == MotionEvent.ACTION_UP) {

                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                            //BottomSheet está escondida...
                            //Remove callbacks
                            handleHideBottomSheet.removeCallbacks(runHideBottomSheet);
                            //Mostra BottomSheet
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            //Hide BottomSheet após 3s
                            handleHideBottomSheet.postDelayed(runHideBottomSheet, 3000);

                        } else if (bottomSheetBehavior.getState()
                                == BottomSheetBehavior.STATE_COLLAPSED) {
                            //BottomSheet está presente...
                            //Hide BottomSheet
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }

                    }


                } else {


                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow NestedScrollView to intercept touch events.
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                            // Allow NestedScrollView to intercept touch events.
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    view.onTouchEvent(motionEvent);

                }


                return true;
            }
        });
    }

    public void playPlayList() {

        if (playList.isEmpty()) {
            //Lista Vazia...
            if (mp.isPlaying() || playingVideo) {
                //...Mas está Tocando...
                playPauseSong();
            } else if(pause){
                //...Ou está em Pausa
                playPauseSong();
            } else {
                //...Não está tocando nem está em pausa
                Toast.makeText(this, R.string.list_empty, Toast.LENGTH_SHORT).show();
            }

        } else if(stop) {
            //Lista não está vazia e Player está parado
            playSong(currentSongIndex);

        } else {
            //Lista não está vazia e Player nõa está parado (Está Tocando ou em Pause)
            playPauseSong();

        }
    }

    private void playPauseSong() {

        //O que está tocando é video?
        if (playingVideo) {
            if (videoView.isPlaying()) {
                videoView.pause();
                // Changing button image to play button
                buttonPlay.setImageResource(R.drawable.ic_play);
                pause = true;
            } else {
                videoView.start();
                // Changing button image to pause button
                buttonPlay.setImageResource(R.drawable.ic_pause);
                pause = false;
            }
            notifyPlayListAdapter();
        } else {

            // check for already playing
            if (mp.isPlaying()) {
                if (mp != null) {
                    mp.pause();
                    // Changing button image to play button
                    buttonPlay.setImageResource(R.drawable.ic_play);
                    pause = true;
                    notifyPlayListAdapter();
                }
            } else {
                // Resume song
                if (mp != null) {
                    mp.start();
                    // Changing button image to pause button
                    buttonPlay.setImageResource(R.drawable.ic_pause);
                    pause = false;
                    notifyPlayListAdapter();
                }
            }
        }
    }

    public void playSong(int songIndex){
        // Play song
        String pathSong = playList.get(songIndex).getAbsolutePathMusic();

        //Se for Video
        if (pathSong.endsWith(".mp4")) {
            //Parar musicPlayer
            mp.stop();
            //Mostrar botões de controle de video
            layoutVideo.setVisibility(View.VISIBLE);
            btnHideShowVideo.setVisibility(View.VISIBLE);
            btnFullscreen.setVisibility(View.VISIBLE);
            if (!fullscreen) {
                btnFullscreen.setImageResource(R.drawable.ic_fullscreen);
            }
            if (videoHide) {
                hideShowVideo();
            }
            //Set icone da música a tocar
            iconMusicCurrent.setImageResource(R.drawable.ic_clipe);

            //Set onPrepare
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (currentPositionVideo > 0) {
                        videoView.seekTo(currentPositionVideo);
                    } else {
                        // Skipping to 1 shows the first frame of the video.
                        videoView.seekTo(1);
                    }
                }
            });

            //Set video in Playback
            videoView.setVideoPath(pathSong);
            videoView.start();
            //Tocando video is true
            playingVideo = true;
            //Atualizar interfaces
            updateUIPlayer(songIndex);

        } else {
            //Se não for vídeo...
            //Ocultar botões de vídeo
            layoutVideo.setVisibility(View.INVISIBLE);
            btnHideShowVideo.setVisibility(View.INVISIBLE);
            btnFullscreen.setVisibility(View.INVISIBLE);
            //Set icone da música a tocar
            iconMusicCurrent.setImageResource(R.drawable.ic_musical_note_white);
            //Para videoPlayer
            videoView.stopPlayback();
            //Tocando vídeo is false
            playingVideo = false;
            //Se estiver no modo fullscreen
            if (fullscreen) {
                //...Sair do modo fullscreen
                fullscreenMode();
            }

            try {
                mp.reset();
                mp.setDataSource(pathSong);
                mp.prepare();
                mp.start();

                updateUIPlayer(songIndex);

            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUIPlayer(int songIndex) {
        //Set Stop para falso
        stop = false;

        //Set Pause para falso
        pause = false;

        //Notify Adapter para mudança de cor do texto
        notifyPlayListAdapter();

        // Displaying Song title
        nameMusicCurrent.setText(playList.get(songIndex).getNameMusic());

        // Changing Button Image to pause image
        buttonPlay.setImageResource(R.drawable.ic_pause);

        // set Progress bar values
        progressBar.setProgress(0);
        progressBar.setMax(100);

        // Updating progress bar
        updateProgressBar();
    }

    public void notifyPlayListAdapter() {
        playListAdapter.setCurrentPlaying(currentSongIndex);
        playListAdapter.setStopped(stop);
        playListAdapter.setPaused(pause);
        playListAdapter.notifyDataSetChanged();
    }

    private BottomSheetBehavior.BottomSheetCallback callbackSheetBehavior() {
        return (new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    buttonExpandBottomSheet.setImageResource(R.drawable.ic_expand_up);
                    buttonClosePlayer.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    buttonExpandBottomSheet.setImageResource(R.drawable.ic_expand_down);
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    buttonClosePlayer.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void setFullScreenBottomSheet() {
        final ViewGroup.LayoutParams childLayoutParams = layoutBottomSheet.getLayoutParams();
        final DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        childLayoutParams.height = displayMetrics.heightPixels;
        layoutBottomSheet.setLayoutParams(childLayoutParams);
    }

    /*Esta função serve para pegar a altura do player que é dinâmica e setar a altura do
        peekHeigth do BottomSheet, a altura do player é dada em pixels.
    */
    private void getHeightOfLayoutView(final View v) {
        ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                v.getViewTreeObserver().removeOnPreDrawListener(this);
                int heightPxFromPlayer = v.getMeasuredHeight();
                bottomSheetBehavior.setPeekHeight(heightPxFromPlayer);
                return true;

            }
        });

    }

    /*private int convertDpToPx(int dp) {
        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) ((dp*density) + 0.5);
    }*/

    /*private int convertPxtoDp(int px) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) ((px/density) + 0.5);
    }*/


    /*private RecyclerView.OnScrollListener scrollPlayListListener() {
        return (new RecyclerView.OnScrollListener() {
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
    }*/

    private View.OnClickListener listenerButtonExpand () {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = bottomSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    buttonClosePlayer.setVisibility(View.VISIBLE);
                } else if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    buttonExpandBottomSheet.setImageResource(R.drawable.ic_expand_down);
                } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    buttonExpandBottomSheet.setImageResource(R.drawable.ic_expand_up);
                }
            }
        });
    }

    private View.OnClickListener listenerButtonClose() {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = bottomSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                buttonClosePlayer.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showPlayList() {

        int state = bottomSheetBehavior.getState();

        if (state == BottomSheetBehavior.STATE_COLLAPSED
                || state == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void chooseDirectory(final Activity activity) {

        final StorageChooser chooser = new StorageChooser.Builder()
                // Specify context of the dialog
                .withActivity(this)
                .withFragmentManager(this.getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                // Define the mode as the FOLDER/DIRECTORY CHOOSER
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        // 2. Handle what should happend when the user selects the directory !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {

                putStringOnShared(activity, path);
                startMain(activity, path);

            }
        });

        // 3. Display File Picker whenever you need to !
        chooser.show();

    }

    private void setStoppedAtribsUI () {
        mp.stop();
        stop = true;
        pause = false;
        currentSongIndex = 0;
        iconMusicCurrent.setImageResource(R.drawable.ic_musical_note_white);
        nameMusicCurrent.setText("");
        buttonPlay.setImageResource(R.drawable.ic_play);
        mHandler.removeCallbacks(mUpdateTimeTask);
        progressBar.setProgress(0);
        timeCurrent.setText(R.string.time);
        timeTotal.setText(R.string.time);
        notifyPlayListAdapter();
    }

    /***********
    *
    *   OVERRRIDES
    *
    * ***********/

    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (fullscreen) {
            fullscreenMode();
        } else {
            toolbar.setTitle(R.string.app_name);
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playingVideo) {
            if (!pause && !stop) {
                videoView.start();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playingVideo && !pause) {
            videoView.pause();
        }
        currentPositionVideo = videoView.getCurrentPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null || videoView !=null) {
            assert mp != null;
            mp.release();
            videoView.stopPlayback();
        }
        mHandler.removeCallbacks(mUpdateTimeTask);
        handleHideBottomSheet.removeCallbacks(runHideBottomSheet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.choose_dir) {
            chooseDirectory(this);
        } else if (id == R.id.show_playlist) {
            showPlayList();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (playingVideo) {

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                appBarLayout.setExpanded(false, true);

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                //Retrato
                appBarLayout.setExpanded(true, true);
                handleHideBottomSheet.removeCallbacks(runHideBottomSheet);
            }

        }

    }

}
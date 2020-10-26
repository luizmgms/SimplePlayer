package com.luizmagno.fragmentwithviewmodel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luizmagno.fragmentwithviewmodel.fragments.MainFragment;
import com.luizmagno.fragmentwithviewmodel.models.Music;
import com.luizmagno.fragmentwithviewmodel.adapters.PlayListAdapter;
import com.luizmagno.fragmentwithviewmodel.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.putStringOnShared;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.startMain;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Toolbar toolbar;
    private ImageView buttonExpandBottomSheet, buttonClosePlayer;
    private LinearLayout layoutBottomSheet;
    private static BottomSheetBehavior bottomSheetBehavior;
    @SuppressLint("StaticFieldLeak")
    private static TextView nameMusicCurrent;
    public static ArrayList<Music> playList;
    @SuppressLint("StaticFieldLeak")
    public static PlayListAdapter playListAdapter;

    public static boolean stop = true;
    public static boolean pause = false;
    public static int currentSongIndex = 0;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar progressBar;
    private static Handler mHandler = new Handler();
    private static Utilities utils = new Utilities();
    @SuppressLint("StaticFieldLeak")
    private static TextView timeCurrent;
    @SuppressLint("StaticFieldLeak")
    private static TextView timeTotal;

    private static FloatingActionButton buttonPlay;

    public static MediaPlayer mp = new MediaPlayer();

    private Activity activity;

    public static VideoView videoView;
    public static boolean playingVideo = false;
    private static ConstraintLayout layoutVideo;
    private static AppBarLayout appBarLayout;
    private boolean videoHide = false;
    private static ImageView btnHideShowVideo;
    private boolean fullscreen = false;
    private static ImageView btnFullscreen;
    private CoordinatorLayout mCoordinatorLayout;
    private static String uriRawVideoExample;
    private View barExpColHidBottomSheet;

    private View decorView;
    DisplayMetrics displayMetrics;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        activity = this;

        //video Example
        uriRawVideoExample = "android.resource://" + getPackageName() + "/" + R.raw.example;

        //LayoutMain
        mCoordinatorLayout = findViewById(R.id.coordinator);

        //Devorations
        decorView = getWindow().getDecorView();
        //DisplayMetrics
        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();

        //WakeOn
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Fragment
        Fragment fragment = MainFragment.newInstance();
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
        //layoutBottomSheet.setOnTouchListener(onTouchListener());

        //Comportamento do BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        //Callback da mudança de estados
        bottomSheetBehavior.addBottomSheetCallback(callbackSheetBehavior());

        //Layout do Player
        final ConstraintLayout layoutPlayer = findViewById(R.id.layoutPlayerId);

        //Pega altura do Player
        getHeightOfLayoutView(layoutPlayer);
        //Set bottomSheet como fullscreen
        setFullScreenBottomSheet();

        //Nome da música tocando
        nameMusicCurrent = findViewById(R.id.nameMusicIsPlayingId);
        nameMusicCurrent.setSelected(true);

        //Times
        timeCurrent = findViewById(R.id.currentTimeId);
        timeTotal = findViewById(R.id.durationTimeId);

        //ProgressBar
        progressBar = findViewById(R.id.progressBar);

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
                notifyPlayListAdapter();
            }
        });

        //Set Listener para Captura de Scroll para PlayList
        playListView.setOnTouchListener(onTouchListener());

        //botão PlayPause
        buttonPlay = findViewById(R.id.fabPlayId);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPlayList(activity);
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
        layoutVideo.setOnTouchListener(onTouchListener());

        //VideoView
        videoView = findViewById(R.id.videoViewId);

        //OnCompletionListenerVideo
        videoView.setOnCompletionListener(onCompletionVideoListener());

        //AppBarLayout
        appBarLayout = findViewById(R.id.app_bar_layout);

        //Botão fechar Video
        btnHideShowVideo = findViewById(R.id.btnHideShowVideoId);
        btnHideShowVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoHide) {
                    ViewCompat.setElevation(layoutVideo, 8);
                    btnHideShowVideo.setImageResource(R.drawable.ic_video_off);
                    videoHide = false;
                } else {
                    ViewCompat.setElevation(layoutVideo, -8);
                    btnHideShowVideo.setImageResource(R.drawable.ic_video_on);
                    videoHide = true;
                }
                
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

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getNavBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void fullscreenMode() {

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
                //Redefine Margins
                postDelayedDimensions();


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
                        setStoppedAtribsUI();
                    } else {
                        //Não é a última então toque a próxima música
                        mp.stop();
                        stop = true;
                        pause = false;
                        currentSongIndex++;
                        notifyPlayListAdapter();
                        playPlayList(activity);
                    }

                } else {
                    //Lista está vazia
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
                        playPlayList(activity);
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
    public static void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private static Runnable mUpdateTimeTask = new Runnable() {
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
            //Se a playList tá vazia
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.choose_dir) {
            chooseDirectory();
        } else if (id == R.id.show_playlist) {
            showPlayList();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void playPlayList(Activity activity) {

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
                Toast.makeText(activity, R.string.list_empty, Toast.LENGTH_SHORT).show();
            }

        } else if(stop) {
            //Lista não está vazia e Player está parado
            playSong(currentSongIndex);

        } else {
            //Lista não está vazia e Player nõa está parado (Está Tocando ou em Pause)
            playPauseSong();

        }
    }

    private static void playPauseSong() {

        //O que está tocando é video?
        if (playingVideo) {
            if (videoView.isPlaying()) {
                videoView.pause();
                // Changing button image to play button
                buttonPlay.setImageResource(R.drawable.ic_play);
                pause = true;
                notifyPlayListAdapter();
            } else {
                videoView.start();
                // Changing button image to pause button
                buttonPlay.setImageResource(R.drawable.ic_pause);
                pause = false;
                notifyPlayListAdapter();
            }
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

    public static void playSong(int songIndex){
        // Play song
        String pathSong = playList.get(songIndex).getAbsolutePathMusic();

        //Se for Video
        if (pathSong.endsWith(".mp4")) {
            mp.stop();
            layoutVideo.setVisibility(View.VISIBLE);
            btnHideShowVideo.setVisibility(View.VISIBLE);
            btnFullscreen.setVisibility(View.VISIBLE);

            videoView.setVideoPath(pathSong);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    videoView.start();
                }
            });

            playingVideo = true;
            updateUIPlayer(songIndex);

        } else {
            //Se não for vídeo...
            layoutVideo.setVisibility(View.INVISIBLE);
            btnHideShowVideo.setVisibility(View.INVISIBLE);
            btnFullscreen.setVisibility(View.VISIBLE);
            videoView.stopPlayback();
            playingVideo = false;
            stop = true;
            pause = false;
            notifyPlayListAdapter();

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

    public static void updateUIPlayer(int songIndex) {
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

    public static void notifyPlayListAdapter() {
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
        peekHeigth do BottomSheet, a altura do player é dada em pixels,
        como a altura do botão é em dp, precisa-se converter de dp para px
    */
    private static void getHeightOfLayoutView(final View v) {
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

    private int convertDpToPx(int dp) {
        displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) ((dp*density) + 0.5);
    }

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

    private void chooseDirectory() {

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


    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (fullscreen) {
            fullscreenMode();
        }else {
            toolbar.setTitle(R.string.app_name);
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp.isPlaying()) {
            mp.pause();
        }
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
        }
        if (videoView != null) {
            videoView.stopPlayback();
        }
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private void setStoppedAtribsUI () {
        mp.stop();
        stop = true;
        pause = false;
        currentSongIndex = 0;
        nameMusicCurrent.setText("");
        buttonPlay.setImageResource(R.drawable.ic_play);
        mHandler.removeCallbacks(mUpdateTimeTask);
        progressBar.setProgress(0);
        timeCurrent.setText(R.string.time);
        timeTotal.setText(R.string.time);
        notifyPlayListAdapter();
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
            }

        }

    }

}
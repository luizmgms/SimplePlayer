package com.luizmagno.fragmentwithviewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luizmagno.fragmentwithviewmodel.ui.main.MainFragment;
import com.luizmagno.fragmentwithviewmodel.utils.Music;
import com.luizmagno.fragmentwithviewmodel.utils.PlayListAdapter;
import com.luizmagno.fragmentwithviewmodel.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView buttonExpandBottomSheet, buttonClosePlayer;
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintLayout layoutPlayer;
    private static TextView nameMusicCurrent;
    private ListView playListView;
    public static ArrayList<Music> playList;
    public static PlayListAdapter playListAdapter;

    public static boolean stop = true;
    public static boolean pause = false;
    public static int currentSongIndex = 0;
    private static ProgressBar progressBar;
    private static Handler mHandler = new Handler();
    private static Utilities utils = new Utilities();
    private static TextView timeCurrent;
    private static TextView timeTotal;

    private static FloatingActionButton buttonPlay;
    private FloatingActionButton buttonNext;
    private FloatingActionButton buttonPrevious;

    public static MediaPlayer mp = new MediaPlayer();

    private Activity activity;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        activity = this;

        //WakeOn
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit();

        //Botão para expandir ou collapse BottomSheet
        buttonExpandBottomSheet = findViewById(R.id.button_expand_bottom_sheet);
        //Click do botão
        buttonExpandBottomSheet.setOnClickListener(listenerButtonExpand());

        //Botão para ocultar Player
        buttonClosePlayer = findViewById(R.id.buttonClosePlayerId);
        //click do botão;
        buttonClosePlayer.setOnClickListener(listenerButtonClose());

        //Layout do BottomShet
        layoutBottomSheet = findViewById(R.id.layout_bottom_sheet);

        //Comportamento do BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        //Callback da mudança de estados
        bottomSheetBehavior.setBottomSheetCallback(callbackSheetBehavior());

        //Layout do Player
        layoutPlayer = findViewById(R.id.layoutPlayerId);

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
        playListView = findViewById(R.id.playListId);

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
        buttonNext = findViewById(R.id.fabNextId);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMusic();
            }
        });

        //botão Previous
        buttonPrevious = findViewById(R.id.fabPreviousId);
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
                playListAdapter.notifyDataSetChanged();
            }
        });

        //OnCompletionListener
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if (currentSongIndex == playList.size() - 1) {
                    //Música que está tocando é a última da lista, então parar de tocar
                    mp.stop();
                    stop = true;
                    pause = false;
                    currentSongIndex = 0;
                    buttonPlay.setImageResource(R.drawable.ic_play);
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    progressBar.setProgress(0);
                    timeCurrent.setText("");
                    timeTotal.setText("");
                    notifyPlayListAdapter();
                } else {
                    //Não é a última então toque a próxima música
                    mp.stop();
                    stop = true;
                    pause = false;
                    currentSongIndex++;
                    notifyPlayListAdapter();
                    playPlayList(activity);
                }

            }
        });


    }

    /**
     * Update timer on progressBar
     * */
    public static void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 50);
    }

    /**
     * Background Runnable thread
     * */
    private static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time ans time completed
            String total = utils.milliSecondsToTimer(totalDuration);
            String atual = utils.milliSecondsToTimer(currentDuration);
            timeCurrent.setText(atual);
            timeTotal.setText(total);

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            progressBar.setProgress(progress);

            // Running this thread after 50 milliseconds
            mHandler.postDelayed(this, 50);
        }
    };

    private void previousMusic () {
        if (playList.isEmpty()) {
            Toast.makeText(this,
                    R.string.list_empty, Toast.LENGTH_SHORT).show();
            if (mp.isPlaying()) {
                mp.stop();
                stop = true;
                pause = false;
                buttonPlay.setImageResource(R.drawable.ic_play);
            }
        } else {
            if (currentSongIndex == 0) {
                currentSongIndex = playList.size() - 1;
            } else {
                currentSongIndex--;
            }
            stop = true;
            pause = false;
            notifyPlayListAdapter();
            playPlayList(activity);

        }
    }

    private void nextMusic() {
        if (playList.isEmpty()) {
            Toast.makeText(this,
                    R.string.list_empty, Toast.LENGTH_SHORT).show();
            if (mp.isPlaying()) {
                mp.stop();
                stop = true;
                pause = false;
                buttonPlay.setImageResource(R.drawable.ic_play);
            }
        } else {
            if (currentSongIndex == playList.size() - 1) {
                currentSongIndex = 0;
            } else {
                currentSongIndex++;
            }
            stop = true;
            pause = false;
            notifyPlayListAdapter();
            playPlayList(activity);
        }
    }

    private ListView.OnTouchListener onTouchListener() {
        return (new ListView.OnTouchListener() {
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
        });
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
            if (mp.isPlaying()) {
                mp.stop();
                stop = true;
                pause = false;
                currentSongIndex = 0;
                buttonPlay.setImageResource(R.drawable.ic_play);
                notifyPlayListAdapter();
            } else {
                Toast.makeText(activity,
                        R.string.list_empty,
                        Toast.LENGTH_SHORT).show();
            }
        } else if(stop) {
            playSong(currentSongIndex);
        } else {
            playPauseSong();
        }
    }

    private static void playPauseSong() {

        // check for already playing
        if(mp.isPlaying()){
            if(mp!=null){
                mp.pause();
                // Changing button image to play button
                buttonPlay.setImageResource(R.drawable.ic_play);
                pause = true;
                notifyPlayListAdapter();
            }
        }else{
            // Resume song
            if(mp!=null){
                mp.start();
                // Changing button image to pause button
                buttonPlay.setImageResource(R.drawable.ic_pause);
                pause = false;
                notifyPlayListAdapter();
            }
        }
    }

    public static void playSong(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(playList.get(songIndex).getAbsolutePathMusic());
            mp.prepare();
            mp.start();

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

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
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
    private void getHeightOfLayoutView(final View v) {
        ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                v.getViewTreeObserver().removeOnPreDrawListener(this);
                int heightPxFromPlayer = layoutPlayer.getMeasuredHeight();
                bottomSheetBehavior.setPeekHeight(heightPxFromPlayer);
                return true;

            }
        });

    }

    private int convertDpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) ((dp*density) + 0.5);
    }

    private int convertPxtoDp(int px) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) ((px/density) + 0.5);
    }


    private RecyclerView.OnScrollListener scrollPlayListListener() {
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
    }

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

                SharedPreferences sharedPreferences = getSharedPreferences(
                        "com.luizmagno.music.preferences", Context.MODE_PRIVATE);

                sharedPreferences.edit().putString("directoryMusic", path).apply();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("directoryMusic", path);
                startActivity(intent);
                finish();

            }
        });

        // 3. Display File Picker whenever you need to !
        chooser.show();

    }


    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            toolbar.setTitle(R.string.app_name);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
        }
    }
}
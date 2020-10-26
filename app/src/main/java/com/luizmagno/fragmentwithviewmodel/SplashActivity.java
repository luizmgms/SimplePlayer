package com.luizmagno.fragmentwithviewmodel;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.luizmagno.fragmentwithviewmodel.fragments.InitFragment;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.NO_EXISTS;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.checkPermissionOfReadCard;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.getPathDirectoryMusicsFromShared;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.startMain;


public class SplashActivity extends AppCompatActivity {

    private String pathDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pathDirectory = getPathDirectoryMusicsFromShared(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                replaceFragment();
            }
        }, 2000);
    }

    private void replaceFragment() {

        if (checkPermissionOfReadCard(this)) {
            //Tem permissão
            //Verifica se a preferencia existe
            if (pathDirectory.equals(NO_EXISTS)) {
                //Não existe. Exibe Fragmento
                showFragment();
            } else {
                //Existe, inicia MainActivity
                startMain(this, pathDirectory);
            }
        } else {
            //Não tem permissão, exibe fragmento
            showFragment();
        }

    }

    private void showFragment() {
        //Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, InitFragment.newInstance())
                .commitNow();

        dismissAnimation();
    }

    private void dismissAnimation() {
        LottieAnimationView lottieAnimationView = findViewById(R.id.playingAnimationId);
        lottieAnimationView.setVisibility(View.INVISIBLE);
    }

}
package com.luizmagno.fragmentwithviewmodel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.luizmagno.fragmentwithviewmodel.ui.main.InitFragment;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String pathDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Preferences
        sharedPreferences = getSharedPreferences("com.luizmagno.music.preferences", Context.MODE_PRIVATE);
        pathDirectory = sharedPreferences.getString("directoryMusic", "noExists");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                replaceFragment();
            }
        }, 2000);
    }

    private void replaceFragment() {

        if (checkPermission()) {
            //Tem permissão
            //Verifica se a preferencia existe
            if (pathDirectory.equals("noExists")) {
                //Não existe. Exibe Fragmento
                showFragment();
            } else {
                //Existe, inicia MainActivity
                startMain();
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

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("directoryMusic", pathDirectory);
        startActivity(intent);
        finish();
    }

    private boolean checkPermission() {

        // Check if the read permission has been granted
        // Permission is already available
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void dismissAnimation() {
        LottieAnimationView lottieAnimationView = findViewById(R.id.playingAnimationId);
        lottieAnimationView.setVisibility(View.INVISIBLE);
    }

}
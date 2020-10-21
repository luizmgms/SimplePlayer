package com.luizmagno.fragmentwithviewmodel.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.luizmagno.fragmentwithviewmodel.MainActivity;
import com.luizmagno.fragmentwithviewmodel.R;

import java.util.Objects;

public class InitFragment extends Fragment {

    View fragment;

    private static final int PERMISSION_REQUEST_READ_CARD = 0;
    private SharedPreferences sharedPreferences;
    private SwitchCompat switchCompat;
    private TextView textOk;
    private Button buttonChoose;
    private String pathDirectory;


    public InitFragment() {
        // Required empty public constructor
    }

    public static InitFragment newInstance() {
        return new InitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.init_fragment, container, false);

        //Preferences
        sharedPreferences = Objects.requireNonNull(
                getActivity()).getSharedPreferences(
                        "com.luizmagno.music.preferences", Context.MODE_PRIVATE);

        pathDirectory = sharedPreferences.getString("directoryMusic", "noExists");

        switchCompat = fragment.findViewById(R.id.switchReadId);
        textOk =  fragment.findViewById(R.id.okId);
        buttonChoose =  fragment.findViewById(R.id.buttonChooseId);
        TextView tudoOk = fragment.findViewById(R.id.buttonTudoOkId);

        if (checkPermission()) {
            textOk.setVisibility(View.VISIBLE);
            switchCompat.setChecked(true);
            switchCompat.setVisibility(View.INVISIBLE);
        } else {
            textOk.setVisibility(View.INVISIBLE);
            switchCompat.setChecked(false);
            switchCompat.setVisibility(View.VISIBLE);
        }

        if (switchCompat.getVisibility() == View.VISIBLE) {
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        requestReadCardPermission();
                }
            });
        }

        if ( !pathDirectory.equals("noExists") ) {
            buttonChoose.setText(pathDirectory);
        }

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDirectory();
            }
        });

        tudoOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pathDirectory.equals("noExists") &&
                        switchCompat.isChecked()) {
                    startMain();
                } else {
                    Toast.makeText(getContext(),
                            "Dê permissão e escolha a pasta!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!pathDirectory.equals("noExists") &&
                switchCompat.getVisibility() == View.INVISIBLE) {
            startMain();
        }

        return fragment;
    }

    private void startMain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("directoryMusic", pathDirectory);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    private boolean checkPermission() {

        // Check if the read permission has been granted
        // Permission is already available
        return ActivityCompat.checkSelfPermission(
                Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
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
                // e.g /storage/emulated/0/Documents
                pathDirectory = path;
                sharedPreferences.edit().putString("directoryMusic", path).apply();
                buttonChoose.setText(path);
            }
        });

        // 3. Display File Picker whenever you need to !
        chooser.show();

    }

    private void requestReadCardPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Objects.requireNonNull(getActivity()),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_CARD);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_CARD);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_READ_CARD) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                //switchCompat.setChecked(true);
                switchCompat.setVisibility(View.INVISIBLE);
                textOk.setVisibility(View.VISIBLE);

            } else {
                // Permission request was denied.
                switchCompat.setChecked(false);
                switchCompat.setVisibility(View.VISIBLE);
                textOk.setVisibility(View.INVISIBLE);
            }
        }

    }
}
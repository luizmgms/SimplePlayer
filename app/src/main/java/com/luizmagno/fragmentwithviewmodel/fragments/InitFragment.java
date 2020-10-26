package com.luizmagno.fragmentwithviewmodel.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.codekidlabs.storagechooser.StorageChooser;
import com.luizmagno.fragmentwithviewmodel.R;

import java.util.Objects;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.NO_EXISTS;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.PERMISSION_REQUEST_READ_CARD;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.checkPermissionOfReadCard;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.getPathDirectoryMusicsFromShared;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.putStringOnShared;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.requestReadCardPermission;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.startMain;

public class InitFragment extends Fragment {

    View fragment;
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
        pathDirectory = getPathDirectoryMusicsFromShared(getActivity());

        switchCompat = fragment.findViewById(R.id.switchReadId);
        textOk =  fragment.findViewById(R.id.okId);
        buttonChoose =  fragment.findViewById(R.id.buttonChooseId);
        TextView tudoOk = fragment.findViewById(R.id.buttonTudoOkId);

        if (checkPermissionOfReadCard(getContext())) {
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
                    if (b) {
                        requestReadCardPermission(getActivity());
                    }
                }
            });
        }

        if ( !pathDirectory.equals(NO_EXISTS) ) {
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
                if (!pathDirectory.equals(NO_EXISTS) &&
                        switchCompat.isChecked()) {
                    startMain(getActivity(), pathDirectory);
                } else {
                    Toast.makeText(getContext(),
                            R.string.give_permission, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!pathDirectory.equals(NO_EXISTS) &&
                switchCompat.getVisibility() == View.INVISIBLE) {
            startMain(getActivity(), pathDirectory);
        }

        return fragment;
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
                pathDirectory = path;
                putStringOnShared(getActivity(), path);
                buttonChoose.setText(path);
            }
        });

        // 3. Display File Picker whenever you need to !
        chooser.show();
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
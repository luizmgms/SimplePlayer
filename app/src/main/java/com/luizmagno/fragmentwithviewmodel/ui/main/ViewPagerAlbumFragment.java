package com.luizmagno.fragmentwithviewmodel.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.utils.Album;
import com.luizmagno.fragmentwithviewmodel.utils.PagerViewAlbumAdapter;
import com.luizmagno.fragmentwithviewmodel.utils.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.Objects;

import static com.luizmagno.fragmentwithviewmodel.MainActivity.toolbar;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.DIRECTORY_MUSICS;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.POSITION;
import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.getListAlbuns;

public class ViewPagerAlbumFragment extends Fragment {

    private ArrayList<Album> listAlbuns;

    public static ViewPagerAlbumFragment newInstance() {
        return new ViewPagerAlbumFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.view_pager_album_fragment,container, false);

        assert getArguments() != null;
        String pathAlbuns = getArguments().getString(DIRECTORY_MUSICS);
        final int position = getArguments().getInt(POSITION);

        listAlbuns = getListAlbuns(pathAlbuns);

        ViewPager viewPager = fragment.findViewById(R.id.viewPagerAlbumId);

        PagerAdapter pagerAdapter = new PagerViewAlbumAdapter(
                Objects.requireNonNull(getActivity()).getSupportFragmentManager(), listAlbuns);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(onPageChangeListener());

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        return fragment;
    }

    private ViewPager.OnPageChangeListener onPageChangeListener() {
        return (new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                toolbar.setTitle(listAlbuns.get(position).getNameAlbum());
            }

            @Override
            public void onPageSelected(int position) { }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

}

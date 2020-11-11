package com.luizmagno.fragmentwithviewmodel.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.luizmagno.fragmentwithviewmodel.MainActivity;
import com.luizmagno.fragmentwithviewmodel.R;
import com.luizmagno.fragmentwithviewmodel.adapters.PagerViewAlbumAdapter;
import com.luizmagno.fragmentwithviewmodel.models.Album;
import com.luizmagno.fragmentwithviewmodel.utils.ZoomOutPageTransformer;

import java.util.ArrayList;

import static com.luizmagno.fragmentwithviewmodel.utils.Utilities.POSITION;

public class ViewPagerAlbumFragment extends Fragment {

    private final ArrayList<Album> listAlbuns;
    private Toolbar toolbar;
    private final MainActivity mainActivity;

    public ViewPagerAlbumFragment(MainActivity mActivity, ArrayList<Album> list) {
        this.mainActivity = mActivity;
        this.listAlbuns = list;
    }

    public static ViewPagerAlbumFragment newInstance(MainActivity mActivity,
                                                     ArrayList<Album> list) {
        return new ViewPagerAlbumFragment(mActivity, list);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Inflate Fragment
        View fragment = inflater.inflate(R.layout.view_pager_album_fragment,container, false);

        //Get Posição do Click
        final int position = getArguments().getInt(POSITION);

        //ViewPager in Fragment
        ViewPager viewPager = fragment.findViewById(R.id.viewPagerAlbumId);

        //Toolbar in MainActivity
        toolbar = getActivity().findViewById(R.id.toolbar);

        //Pager Adapter
        PagerAdapter pagerAdapter = new PagerViewAlbumAdapter(
                mainActivity.getSupportFragmentManager(), listAlbuns, mainActivity);

        //Set Pager Adapter in viewPager
        viewPager.setAdapter(pagerAdapter);

        //Set Posição atual
        viewPager.setCurrentItem(position);

        //Listener de set Toolbar Title
        viewPager.addOnPageChangeListener(onPageChangeListener());

        //Set Animação de Transição
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

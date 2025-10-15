package com.rarestardev.magneticplayer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rarestardev.magneticplayer.view.fragments.FavoriteFragment;
import com.rarestardev.magneticplayer.view.fragments.PlaylistFragment;
import com.rarestardev.magneticplayer.view.fragments.TracksFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        switch (i) {
            case 1:
                return new PlaylistFragment();
            case 2:
                return new FavoriteFragment();
            default:
                return new TracksFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

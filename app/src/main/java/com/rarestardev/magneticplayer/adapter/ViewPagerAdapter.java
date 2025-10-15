package com.rarestardev.magneticplayer.adapter;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<Pair<Fragment,String>> fragmentTitlePairs;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,List<Pair<Fragment,String>> fragmentTitlePairs) {
        super(fragmentActivity);
        this.fragmentTitlePairs = fragmentTitlePairs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        return fragmentTitlePairs.get(i).first;
    }

    @Override
    public int getItemCount() {
        return fragmentTitlePairs.size();
    }

    public String getPageTitle(int position){
        return fragmentTitlePairs.get(position).second;
    }
}

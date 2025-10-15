package com.rarestardev.magneticplayer.music_utils.popular;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.databinding.ActivityAllPopularBinding;
import com.rarestardev.magneticplayer.model.RecentList;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.RecentListViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class AllPopularActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAllPopularBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_all_popular);

        binding.setListSize("0");

        binding.backActivity.setOnClickListener(v -> finish());

        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecentListViewModel viewModel = new ViewModelProvider(this).get(RecentListViewModel.class);
        PopularAdapter popularAdapter = new PopularAdapter(this);

        viewModel.getAllRecentLists().observe(this, recentLists -> {
            if (recentLists != null && !recentLists.isEmpty()) {
                List<RecentList> lists = recentLists.stream()
                        .sorted((o1, o2) -> Long.compare(o2.getPlayCount(), o1.getPlayCount()))
                        .collect(Collectors.toList());

                popularAdapter.setRecentLists(lists);
                binding.recyclerView.setAdapter(popularAdapter);
                binding.recyclerView.setHasFixedSize(true);
                binding.setListSize(String.valueOf(lists.size()));
            }

            MusicApplication application = (MusicApplication) getApplication();
            MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

            musicStatusViewModel.getFilePath().observe(this, s -> {
                if (s != null) {
                    popularAdapter.setSongPath(s);
                }
            });

            musicStatusViewModel.getIsPlayMusic().observe(this, popularAdapter::setPlayedMusic);
        });
    }
}
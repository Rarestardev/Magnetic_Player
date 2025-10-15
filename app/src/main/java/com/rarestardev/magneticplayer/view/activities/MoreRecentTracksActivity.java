package com.rarestardev.magneticplayer.view.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.MusicFileAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.databinding.ActivityMoreTracksBinding;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.EndOfListMarginDecorator;
import com.rarestardev.magneticplayer.utilities.NavigationBarUtils;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.RecentListViewModel;

import java.util.List;

public class MoreRecentTracksActivity extends BaseActivity {

    private ActivityMoreTracksBinding binding;
    private MusicApplication application;
    private MusicStatusViewModel musicStatusViewModel;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_more_tracks);

        NavigationBarUtils.setNavigationBarColor(this,0);
        setMusicPlayerUi(binding.viewStub.getViewStub());
        doInitialization();

        binding.backActivity.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        });

        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);
    }

    private void doInitialization() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.addItemDecoration(new EndOfListMarginDecorator());
        RecentListViewModel viewModel = new ViewModelProvider(this).get(RecentListViewModel.class);
        viewModel.getAllRecentLists().observe(this, recentLists -> {
            if (!recentLists.isEmpty()) {
                List<MusicFile> musicFileList = viewModel.convertToMusicFileList(recentLists);
                MusicFileAdapter musicAdapter = new MusicFileAdapter(this);
                musicAdapter.setList(musicFileList, recentLists.size());
                binding.recyclerView.setAdapter(musicAdapter);
                binding.setTrackListSize("( " + recentLists.size() + " )");

                application = (MusicApplication) getApplication();
                musicStatusViewModel = application.getMusicViewModel();
                musicStatusViewModel.getCurrentPosition().observe(this, integer -> binding.recyclerView.scrollToPosition(integer));

                musicStatusViewModel.getFilePath().observe(this, musicAdapter::getMusicIsPlaying);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
}
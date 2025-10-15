package com.rarestardev.magneticplayer.music_utils.music_folder;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.databinding.ActivityFolderBinding;
import com.rarestardev.magneticplayer.listener.FolderClickListener;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.MusicFolder;
import com.rarestardev.magneticplayer.music_utils.music.adapters.MusicFileAdapter;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.viewmodel.FolderViewModel;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FolderActivity extends BaseActivity implements FolderClickListener {

    private ActivityFolderBinding binding;
    private FolderViewModel viewModel;
    private MusicFolderAdapter adapter;
    private List<MusicFile> musicFiles = new ArrayList<>();
    private boolean isBackActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_folder);

        viewModel = new ViewModelProvider(this).get(FolderViewModel.class);
        adapter = new MusicFolderAdapter(this);

        binding.setIsFolderView(true);
        binding.setTitle(getString(R.string.tab_folders));

        initFolders();

        initAdBanner();

        binding.backActivity.setOnClickListener(v -> {
            if (isBackActivity) {
                musicFiles.clear();
                isBackActivity = false;
                binding.setTitle(getString(R.string.tab_folders));
                binding.setIsFolderView(true);
                initFolders();
            } else {
                finish();
            }
        });
    }

    private void initFolders() {
        binding.recyclerViewFolder.setLayoutManager(new LinearLayoutManager(this));
        viewModel.getAllFolder().observe(this, musicFolders -> {
            if (musicFolders != null || !musicFolders.isEmpty()) {

                musicFolders.sort(Comparator.comparing(MusicFolder::getFolderName));

                binding.setNotFindFolder(false);
                adapter.setMusicFolders(musicFolders);
                binding.recyclerViewFolder.setAdapter(adapter);
                binding.recyclerViewFolder.setHasFixedSize(true);
            } else {
                binding.setNotFindFolder(true);
            }
        });
        viewModel.loadFolder();
    }

    @Override
    public void onClickFolder(String folderName, String folderPath) {
        isBackActivity = true;
        binding.setIsFolderView(false);
        binding.setLoading(true);
        binding.setTitle(folderName);

        binding.recyclerViewMusic.setLayoutManager(new LinearLayoutManager(FolderActivity.this));
        MusicFileAdapter musicFileAdapter = new MusicFileAdapter(FolderActivity.this);
        viewModel.getAllMusicOnFolder().observe(this, mf -> {
            if (mf != null && !mf.isEmpty()) {
                musicFiles = mf;
                musicFiles.sort(Comparator.comparing(MusicFile::getSongTitle));
                musicFileAdapter.setList(musicFiles, musicFiles.size());
                binding.recyclerViewMusic.setAdapter(musicFileAdapter);
                binding.recyclerViewMusic.refreshDrawableState();
                binding.setLoading(false);
            }
        });

        viewModel.loadFolderOnMusic(folderPath);

        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();
        musicFileAdapter.setMusicStatusViewModel(musicStatusViewModel);
    }

    private void initAdBanner() {
        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicFiles.clear();
        isBackActivity = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        musicFiles.clear();
        isBackActivity = false;
    }

    @Override
    public void onBackPressed() {
        if (isBackActivity) {
            musicFiles.clear();
            isBackActivity = false;
            binding.setTitle(getString(R.string.tab_folders));
            binding.setIsFolderView(true);
            initFolders();
        } else {
            super.onBackPressed();
        }
    }
}
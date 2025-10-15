package com.rarestardev.magneticplayer.view.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adivery.sdk.AdiveryBannerAdView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.MusicFileAdapter;
import com.rarestardev.magneticplayer.adapter.MusicFolderAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.listener.FolderClickListener;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.utilities.EndOfListMarginDecorator;
import com.rarestardev.magneticplayer.viewmodel.FolderViewModel;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

public class FolderFragment extends Fragment implements FolderClickListener {

    private FolderViewModel viewModel;
    private MusicFolderAdapter adapter;
    private AdNetworkManager adNetworkManager;

    private RecyclerView recyclerView;
    private AppCompatTextView not_find_folder;
    private AdiveryBannerAdView adsBannerLayoutFolderFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        not_find_folder = view.findViewById(R.id.not_find_folder);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new EndOfListMarginDecorator());
        recyclerView.setHasFixedSize(true);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()))
                .get(FolderViewModel.class);

        viewModel.getAllFolder().observe(getViewLifecycleOwner(), musicFolders -> {
            if (musicFolders != null || !musicFolders.isEmpty()) {
                not_find_folder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new MusicFolderAdapter(musicFolders, this);
                recyclerView.setAdapter(adapter);
            } else {
                not_find_folder.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Log.e(Constants.appLog,"NoFolder");
            }
        });
        viewModel.loadFolder();
        adsBannerLayoutFolderFragment = view.findViewById(R.id.banner_ad);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        adNetworkManager = new AdNetworkManager(getContext());
        adNetworkManager.showSmallBannerAds(adsBannerLayoutFolderFragment);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClickFolder(String folderName, String folderPath) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.show_music_on_folder_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

        AppCompatImageView close_dialog = dialog.findViewById(R.id.close_dialog);
        AppCompatTextView folder_name_tv = dialog.findViewById(R.id.folder_name_tv);
        AppCompatTextView folder_size = dialog.findViewById(R.id.folder_size);
        RecyclerView music_recyclerView = dialog.findViewById(R.id.music_recyclerView);

        AdiveryBannerAdView adView = dialog.findViewById(R.id.banner_ad);
        adNetworkManager.showSmallBannerAds(adView);

        close_dialog.setOnClickListener(v -> dialog.dismiss());
        folder_name_tv.setText(folderName);

        music_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        music_recyclerView.setHasFixedSize(true);

        MusicFileAdapter musicFileAdapter = new MusicFileAdapter(getContext());

        viewModel.getAllMusicOnFolder().observe(this, musicFiles -> {
            if (musicFiles != null) {
                musicFileAdapter.setList(musicFiles, musicFiles.size());
                music_recyclerView.setAdapter(musicFileAdapter);
                music_recyclerView.refreshDrawableState();
                folder_size.setText(String.format("( %d )", musicFiles.size()));
            }
        });

        viewModel.loadFolderOnMusic(folderPath);

        MusicApplication application = (MusicApplication) getActivity().getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        musicStatusViewModel.getFilePath().observe(this, musicFileAdapter::getMusicIsPlaying);

        dialog.show();
    }
}
package com.rarestardev.magneticplayer.view.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.viewmodel.MusicViewModel;

import java.util.List;

public abstract class BaseFragment extends Fragment {

    protected MusicViewModel musicViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(MusicViewModel.class);

        updateData();
    }

    public void updateData(){
        musicViewModel.getAllMusic().observe(this, this::onMusicDataLoaded);
        musicViewModel.loadMusic();
    }

    protected abstract void onMusicDataLoaded(List<MusicFile> musicFiles);
}

package com.rarestardev.magneticplayer.view.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.settings.storage.ThemesStorage;
import com.rarestardev.magneticplayer.viewmodel.MusicViewModel;

import java.util.List;

public abstract class BaseFragment extends Fragment {

    protected MusicViewModel musicViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleThemeMode();
        musicViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(MusicViewModel.class);

        updateData();
    }

    private void handleThemeMode() {
        ThemesStorage storage = new ThemesStorage(getContext());
        if (storage.getThemeName() != null) {
            switch (storage.getThemeName()) {
                case "BaseColor":
                    getActivity().setTheme(R.style.Base_Theme_MagneticPlayer);
                    break;
                case "Orange":
                    getActivity().setTheme(R.style.Orange_Theme_MagneticPlayer);
                    break;
                case "DarkSlate":
                    getActivity().setTheme(R.style.Dark_Slate_Gray_Theme_MagneticPlayer);
                    break;
                case "Red":
                    getActivity().setTheme(R.style.Red_Theme_MagneticPlayer);
                    break;
                case "Purple":
                    getActivity().setTheme(R.style.Purple_Theme_MagneticPlayer);
                    break;
                case "Green":
                    getActivity().setTheme(R.style.Green_Theme_MagneticPlayer);
                    break;
            }
        }
    }

    public void updateData(){
        musicViewModel.getAllMusic().observe(this, this::onMusicDataLoaded);
        musicViewModel.loadMusic();
    }

    protected abstract void onMusicDataLoaded(List<MusicFile> musicFiles);
}

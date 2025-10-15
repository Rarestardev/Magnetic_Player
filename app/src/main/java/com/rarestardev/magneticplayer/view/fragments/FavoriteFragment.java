package com.rarestardev.magneticplayer.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.FavoriteMusicAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.controller.MusicPlayerService;
import com.rarestardev.magneticplayer.databinding.FragmentPopularBinding;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.EndOfListMarginDecorator;
import com.rarestardev.magneticplayer.viewmodel.FavoriteMusicViewModel;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment implements FavoriteMusicAdapter.OnFavoriteMusicPlayListener {

    private FragmentPopularBinding binding;
    private FavoriteMusicViewModel favoriteMusicViewModel;
    private FavoriteMusicAdapter adapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_popular, container, false);

        AdNetworkManager adNetworkManager = new AdNetworkManager(getContext());
        adNetworkManager.showSmallBannerAds(binding.bannerAd);

        return binding.getRoot();
    }

    private void doInitialization() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addItemDecoration(new EndOfListMarginDecorator());
        adapter = new FavoriteMusicAdapter(getContext());
        adapter.FavoriteClickListener(this);

        favoriteMusicViewModel = new ViewModelProvider(this).get(FavoriteMusicViewModel.class);
        loadFavoriteMusic();

        MusicApplication application = (MusicApplication) getActivity().getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        musicStatusViewModel.getFilePath().observe(getViewLifecycleOwner(), s -> adapter.getMusicIsPlaying(s));
    }

    private void loadFavoriteMusic() {
        favoriteMusicViewModel.getAllFavoriteList().observe(getViewLifecycleOwner(), musicFiles -> {
            if (musicFiles != null && !musicFiles.isEmpty()) {
                adapter.setList(musicFiles);
                binding.recyclerView.setAdapter(adapter);
                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.refreshDrawableState();
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.notSavedMusic.setVisibility(View.GONE);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                binding.notSavedMusic.setVisibility(View.VISIBLE);
            }
        });

        favoriteMusicViewModel.loadFavoriteList();
    }

    @Override
    public void onMusicPlay(List<MusicFile> musicFiles, int position) {
        Intent service = new Intent(getContext(), MusicPlayerService.class);
        service.putParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue(), new ArrayList<>(musicFiles));
        service.putExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), position);
        getActivity().startService(service);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDeleteFavorite(MusicFile musicFile) {
        favoriteMusicViewModel.deleteFavoriteMusic(musicFile);
        adapter.notifyDataSetChanged();
        loadFavoriteMusic();
    }

    @Override
    public void onResume() {
        super.onResume();
        doInitialization();
    }
}
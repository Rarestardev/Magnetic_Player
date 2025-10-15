package com.rarestardev.magneticplayer.view.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adivery.sdk.AdiveryBannerAdView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.ArtistAdapter;
import com.rarestardev.magneticplayer.adapter.MusicFileAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.model.ArtistMusicModel;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistFragment extends BaseFragment {

    private RecyclerView artist_recycler_view;
    private AppCompatTextView not_find;
    private AdiveryBannerAdView adsBannerLayoutArtistFragment;

    private AdNetworkManager adNetworkManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        artist_recycler_view = view.findViewById(R.id.artist_recycler_view);
        not_find = view.findViewById(R.id.not_find);

        artist_recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 3));
        artist_recycler_view.setHasFixedSize(true);

        adsBannerLayoutArtistFragment = view.findViewById(R.id.banner_ad);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adNetworkManager = new AdNetworkManager(getContext());
        adNetworkManager.showSmallBannerAds(adsBannerLayoutArtistFragment);
    }

    @Override
    protected void onMusicDataLoaded(List<MusicFile> musicFiles) {
        if (musicFiles != null || !musicFiles.isEmpty()) {

            List<ArtistMusicModel> artistMusicModels = getArtistFromMusic(musicFiles);
            if (artistMusicModels.isEmpty()) {
                not_find.setVisibility(View.VISIBLE);
                artist_recycler_view.setVisibility(View.GONE);
            } else {
                ArtistAdapter artistAdapter = new ArtistAdapter(artistMusicModels, getContext());
                artist_recycler_view.setAdapter(artistAdapter);
                artist_recycler_view.refreshDrawableState();
                artist_recycler_view.setVisibility(View.VISIBLE);
                not_find.setVisibility(View.GONE);
                artistAdapter.setListener((artistName, cover) -> showDialogArtistMusic(musicFiles, artistName, cover));
            }
        }
    }

    public List<ArtistMusicModel> getArtistFromMusic(List<MusicFile> musicFiles) {
        Map<String, ArtistMusicModel> artistMap = new HashMap<>();

        for (MusicFile file : musicFiles) {
            if (file != null) {
                String artistName = file.getArtistName();
                String artistCover = file.getAlbumArtUri();

                if (artistMap.containsKey(artistName)) {
                    ArtistMusicModel existingAlbum = artistMap.get(artistName);
                    int currentTrackCount = existingAlbum.getSongCount();
                    existingAlbum.setSongCount(currentTrackCount + 1);
                } else {
                    int songCount = 0;
                    for (ArtistMusicModel artistMusicModel : artistMap.values()) {
                        songCount = artistMusicModel.getSongCount();
                        artistMusicModel.setSongCount(songCount);
                    }
                    ArtistMusicModel artistMusicModel = new ArtistMusicModel(artistName, artistCover, songCount);
                    artistMap.put(artistName, artistMusicModel);
                }
            }
        }

        return new ArrayList<>(artistMap.values());
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    private void showDialogArtistMusic(List<MusicFile> musicFiles, String artist, String cover) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.artist_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialog.setCancelable(true);

        AppCompatImageView close_dialog = dialog.findViewById(R.id.close_dialog);
        RoundedImageView coverImageView = dialog.findViewById(R.id.coverImageView);
        AppCompatTextView tvArtistNameDialog = dialog.findViewById(R.id.tvArtistNameDialog);
        AppCompatTextView tvListSizeDialog = dialog.findViewById(R.id.tvListSizeDialog);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        AdiveryBannerAdView banner_ad = dialog.findViewById(R.id.banner_ad);

        adNetworkManager.showSmallBannerAds(banner_ad);

        close_dialog.setOnClickListener(v -> dialog.dismiss());
        tvArtistNameDialog.setText(artist);

        Glide.with(this)
                .load(cover)
                .placeholder(getContext().getDrawable(R.drawable.ic_music))
                .into(coverImageView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        MusicFileAdapter musicFileAdapter = new MusicFileAdapter(getContext());

        List<MusicFile> newMusicFiles = new ArrayList<>();

        for (MusicFile file : musicFiles) {
            if (file.getArtistName().equals(artist)) {
                newMusicFiles.add(file);
            }
        }

        musicFileAdapter.setList(newMusicFiles, newMusicFiles.size());
        recyclerView.setAdapter(musicFileAdapter);

        tvListSizeDialog.setText(String.format("( %d )", newMusicFiles.size()));

        MusicApplication application = (MusicApplication) getActivity().getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        musicStatusViewModel.getFilePath().observe(this, musicFileAdapter::getMusicIsPlaying);

        dialog.show();
    }
}
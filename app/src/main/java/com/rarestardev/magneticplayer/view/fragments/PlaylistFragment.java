package com.rarestardev.magneticplayer.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adivery.sdk.AdiveryBannerAdView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.FavoriteMusicAdapter;
import com.rarestardev.magneticplayer.adapter.PlaylistWithMusicAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.controller.MusicPlayerService;
import com.rarestardev.magneticplayer.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.listener.DeletePlaylistListener;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.PlayListViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private PlaylistWithMusicAdapter adapter;
    private PlayListViewModel viewModel;

    private RecyclerView playlistRecyclerView;
    private AppCompatTextView noPlaylist;
    private AdiveryBannerAdView adsBannerLayoutPlaylistFragment;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);
        noPlaylist = view.findViewById(R.id.noPlaylist);

        viewModel = new ViewModelProvider(this).get(PlayListViewModel.class);

        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistRecyclerView.setHasFixedSize(true);

        adapter = new PlaylistWithMusicAdapter(getContext());

        getAllMusicWithPlaylist();

        viewModel.getAllPlaylist().observe(getViewLifecycleOwner(), playlistEntities -> {
            if (playlistEntities != null && !playlistEntities.isEmpty()) {
                adapter.setPlaylist(playlistEntities);
                playlistRecyclerView.setAdapter(adapter);
                getAllMusicWithPlaylist();
                adapter.notifyDataSetChanged();
                noPlaylist.setVisibility(View.GONE);
                playlistRecyclerView.setVisibility(View.VISIBLE);
            } else {
                noPlaylist.setVisibility(View.VISIBLE);
                playlistRecyclerView.setVisibility(View.GONE);
            }
        });
        adsBannerLayoutPlaylistFragment = view.findViewById(R.id.banner_ad);
        return view;
    }

    private void getAllMusicWithPlaylist() {
        adapter.setListener(new DeletePlaylistListener() {
            @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
            @Override
            public void onDeletePlaylist(PlaylistEntity playlist) {
                if (playlist != null) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.Theme_CustomThemeAlertDialog)
                            .setIcon(R.drawable.ic_warning)
                            .setCancelable(true)
                            .setTitle(playlist.getPlaylistName())
                            .setMessage("All songs in the list will be deleted. Are you sure?")
                            .setBackground(getActivity().getDrawable(R.drawable.custom_alert_dialog))
                            .setPositiveButton("Yes", (dialog, which) -> {
                                viewModel.deleteAllTracksOnPlaylist(playlist.getPlaylistName());
                                viewModel.deletePlaylist(playlist);
                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                                viewModel.loadPlaylist();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    builder.show();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void getPlaylistItemId(String playlistName, RecyclerView recyclerViewMusic) {
                viewModel.getMusicOnPlaylist().observe(getViewLifecycleOwner(), musicOnPlayListEntities -> {
                    if (musicOnPlayListEntities != null) {
                        List<MusicFile> files = viewModel.convertMusicToListFromPlaylist(musicOnPlayListEntities);
                        FavoriteMusicAdapter musicFileAdapter = new FavoriteMusicAdapter(getContext());// use favorite adapter for this file
                        musicFileAdapter.setList(files);
                        recyclerViewMusic.setAdapter(musicFileAdapter);
                        recyclerViewMusic.refreshDrawableState();

                        adapter.notifyDataSetChanged();
                        MusicApplication application = (MusicApplication) getActivity().getApplication();
                        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

                        musicStatusViewModel.getFilePath().observe(getViewLifecycleOwner(), musicFileAdapter::getMusicIsPlaying);

                        musicFileAdapter.FavoriteClickListener(new FavoriteMusicAdapter.OnFavoriteMusicPlayListener() {
                            @Override
                            public void onMusicPlay(List<MusicFile> musicFiles, int position) {
                                Intent intentStartMusicService = new Intent(getContext(), MusicPlayerService.class)
                                        .putParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue(), new ArrayList<>(musicFiles))
                                        .putExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), position);

                                getContext().startService(intentStartMusicService);
                            }

                            @Override
                            public void onDeleteFavorite(MusicFile musicFile) {
                                viewModel.deleteTracksOnPlaylist(playlistName, musicFile.getFilePath());
                                Toast.makeText(getContext(), "Deleted music on playlist", Toast.LENGTH_SHORT).show();
                                viewModel.loadPlaylist();
                                musicFileAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        Log.e(Constants.appLog, "playlistEntities is null");
                    }
                });

                viewModel.loadMusicOnPlaylist(playlistName);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AdNetworkManager adNetworkManager = new AdNetworkManager(getContext());
        adNetworkManager.showSmallBannerAds(adsBannerLayoutPlaylistFragment);

        viewModel.loadPlaylist();
    }
}
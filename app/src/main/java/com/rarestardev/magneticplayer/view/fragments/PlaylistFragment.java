package com.rarestardev.magneticplayer.view.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adivery.sdk.AdiveryBannerAdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.FavoriteMusicAdapter;
import com.rarestardev.magneticplayer.adapter.PlaylistWithMusicAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.helper.PlayMusicWithEqualizer;
import com.rarestardev.magneticplayer.database.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.listener.DeletePlaylistListener;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.utilities.EndOfListMarginDecorator;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.PlayListViewModel;

import java.util.List;

public class PlaylistFragment extends BaseFragment {

    private PlaylistWithMusicAdapter adapter;
    private PlayListViewModel viewModel;
    private RecyclerView playlistRecyclerView;
    private AppCompatTextView noPlaylist;
    private AdiveryBannerAdView adsBannerLayoutPlaylistFragment;
    private MaterialButton btn_add_playlist;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);
        noPlaylist = view.findViewById(R.id.noPlaylist);
        btn_add_playlist = view.findViewById(R.id.btn_add_playlist);

        viewModel = new ViewModelProvider(this).get(PlayListViewModel.class);

        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistRecyclerView.setHasFixedSize(true);
        playlistRecyclerView.addItemDecoration(new EndOfListMarginDecorator());

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

        addedNewPlaylist();

        return view;
    }

    @SuppressLint("CheckResult")
    private void addedNewPlaylist() {
        btn_add_playlist.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.added_playlist_layout);
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            AppCompatEditText add_playlist_edittext = dialog.findViewById(R.id.add_playlist_edittext);
            MaterialButton btn_save_new_playlist = dialog.findViewById(R.id.btn_save_new_playlist);

            add_playlist_edittext.setFocusable(true);

            btn_save_new_playlist.setOnClickListener(v1 -> {
                String name = add_playlist_edittext.getText().toString();
                if (!name.isEmpty()) {
                    PlaylistEntity playlist = new PlaylistEntity();
                    playlist.setPlaylistName(name);
                    playlist.setCurrent_date(System.currentTimeMillis());

                    viewModel.insertPlaylist(playlist)
                            .subscribe(() -> {
                                        Toast.makeText(getContext(), getString(R.string.saved_new_playlist), Toast.LENGTH_SHORT).show();
                                        loadPlaylist();
                                    },
                                    throwable ->
                                            Log.e(Constants.appLog, "Error to save new playlist"));

                } else {
                    add_playlist_edittext.getText().clear();
                }
                dialog.dismiss();
                add_playlist_edittext.setFocusable(false);
            });

        });
    }

    private void loadPlaylist() {
        viewModel.loadPlaylist();
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
                            .setMessage(R.string.are_you_sure)
                            .setBackground(getActivity().getDrawable(R.drawable.custom_alert_dialog))
                            .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                                viewModel.deleteAllTracksOnPlaylist(playlist.getPlaylistName());
                                viewModel.deletePlaylist(playlist);
                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                                viewModel.loadPlaylist();
                            })
                            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
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
                        musicStatusViewModel.getIsPlayMusic().observe(getViewLifecycleOwner(), musicFileAdapter::setPlayedMusic);

                        musicFileAdapter.FavoriteClickListener(new FavoriteMusicAdapter.OnFavoriteMusicPlayListener() {
                            @Override
                            public void onMusicPlay(List<MusicFile> musicFiles, int position) {
                                PlayMusicWithEqualizer playMusicWithEqualizer = new PlayMusicWithEqualizer(getContext());
                                playMusicWithEqualizer.startMusicService(musicFiles, position,false);
                            }

                            @Override
                            public void onDeleteFavorite(MusicFile musicFile) {
                                viewModel.deleteTracksOnPlaylist(playlistName, musicFile.getFilePath());
                                Toast.makeText(getContext(), R.string.deleted_music_on_playlist, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onMusicDataLoaded(List<MusicFile> musicFiles) {

    }
}
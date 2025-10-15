package com.rarestardev.magneticplayer.custom_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adivery.sdk.AdiveryBannerAdView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.MusicFileAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.controller.MusicCoverManager;
import com.rarestardev.magneticplayer.controller.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.controller.MusicPlayerService;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.model.AlbumInfo;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShowTracksOnAlbumDialog extends Dialog {

    private final Context context;
    private List<MusicFile> musicFiles;
    private final List<MusicFile> newMusicList = new ArrayList<>();
    private AlbumInfo albumInfo;
    private AppCompatImageView close_dialog;
    private RoundedImageView album_cover;
    private AppCompatTextView artist_name, album_name;
    private MaterialButton shuffle_play;
    private RecyclerView trackListRecyclerView;
    private LinearLayoutCompat dialog_layout;
    private AdiveryBannerAdView banner_ad;

    public ShowTracksOnAlbumDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void getAlbumInfo(AlbumInfo info){
        albumInfo = info;
    }

    public void getCurrentList(List<MusicFile> musicFiles){
        this.musicFiles = musicFiles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_tracks_on_album_dialog);

        initDialog();
        findViews();
        initView();

        AdNetworkManager adNetworkManager = new AdNetworkManager(context);
        adNetworkManager.showSmallBannerAds(banner_ad);
    }

    private void initDialog() {
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getWindow().setWindowAnimations(R.style.DialogAnimation);
        setCancelable(true);
    }

    private void findViews() {
        close_dialog = findViewById(R.id.close_dialog);
        album_cover = findViewById(R.id.album_cover);
        artist_name = findViewById(R.id.artist_name);
        album_name = findViewById(R.id.album_name);
        shuffle_play = findViewById(R.id.shuffle_play);
        trackListRecyclerView = findViewById(R.id.trackListRecyclerView);
        dialog_layout = findViewById(R.id.dialog_layout);
        banner_ad = findViewById(R.id.banner_ad);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        album_name.setText(albumInfo.getAlbumName());
        if (!musicFiles.isEmpty()){
            for (MusicFile musicFile : musicFiles) {
                if (musicFile.getAlbumName().equals(albumInfo.getAlbumName())) {
                    newMusicList.add(musicFile);
                }
            }
        }else {
            Log.e(Constants.appLog,"Music Files is empty");
        }

        if (newMusicList.isEmpty()){
            Log.e(Constants.appLog,"New Music Files is empty");
        }

        MusicFileAdapter adapter = new MusicFileAdapter(context);
        adapter.setList(newMusicList, newMusicList.size());

        trackListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        trackListRecyclerView.setHasFixedSize(true);
        trackListRecyclerView.setAdapter(adapter);
        trackListRecyclerView.refreshDrawableState();

        if (newMusicList.size() > 3){
            shuffle_play.setVisibility(View.VISIBLE);
            shuffle_play.setOnClickListener(v -> {
                Random random = new Random();
                int index = random.nextInt(newMusicList.size());
                startPlayMusic(newMusicList, index);

                MusicPlaybackSettings musicPlaybackSettings = new MusicPlaybackSettings(context);
                if (!musicPlaybackSettings.getIsShuffle()){
                    musicPlaybackSettings.setIsShuffle(true);
                }
            });
        }else {
            shuffle_play.setVisibility(View.GONE);
        }

        close_dialog.setOnClickListener(v -> this.dismiss());
        artist_name.setText(albumInfo.getArtistName());

        Glide.with(context)
                .load(albumInfo.getAlbumArtUri())
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .into(album_cover);

        MusicCoverManager coverManager = new MusicCoverManager(context);
        coverManager.loadCoverAndSetBackground(albumInfo.getAlbumArtUri(),album_cover,dialog_layout);

        MusicApplication application = (MusicApplication) ((Activity) context).getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        musicStatusViewModel.getFilePath().observe((LifecycleOwner) context, adapter::getMusicIsPlaying);
    }

    private void startPlayMusic(List<MusicFile> musicFiles, int position) {
        Intent service = new Intent(context, MusicPlayerService.class);
        service.putParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue(), new ArrayList<>(musicFiles));
        service.putExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), position);
        context.startService(service);
        this.dismiss();
    }
}

package com.rarestardev.magneticplayer.view.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adivery.sdk.AdiveryBannerAdView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.AlbumsAdapter;
import com.rarestardev.magneticplayer.custom_views.ShowTracksOnAlbumDialog;
import com.rarestardev.magneticplayer.model.AlbumInfo;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumFragment extends BaseFragment {

    private RecyclerView albumRecyclerView;
    private AdiveryBannerAdView adsBannerLayout;
    private AppCompatTextView not_find;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albumRecyclerView = view.findViewById(R.id.albumRecyclerView);
        not_find = view.findViewById(R.id.not_find);
        adsBannerLayout = view.findViewById(R.id.banner_ad);
        albumRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        AdNetworkManager adNetworkManager = new AdNetworkManager(getContext());
        adNetworkManager.showSmallBannerAds(adsBannerLayout);
    }

    @Override
    protected void onMusicDataLoaded(List<MusicFile> musicFiles) {
        AlbumsAdapter albumsAdapter = new AlbumsAdapter(getContext());
        if (musicFiles != null || !musicFiles.isEmpty()) {
            List<AlbumInfo> albumInfos = getAlbumFromMusic(musicFiles);
            albumsAdapter.setAlbumData(albumInfos);
            albumRecyclerView.setAdapter(albumsAdapter);
            albumRecyclerView.setHasFixedSize(true);

            albumsAdapter.AlbumClickListener(info -> {
                ShowTracksOnAlbumDialog showTracksOnAlbumDialog = new ShowTracksOnAlbumDialog(getContext());

                showTracksOnAlbumDialog.getAlbumInfo(info);

                showTracksOnAlbumDialog.getCurrentList(musicFiles);

                showTracksOnAlbumDialog.show();
            });

            not_find.setVisibility(View.GONE);
            albumRecyclerView.setVisibility(View.VISIBLE);
        }else {
            not_find.setVisibility(View.VISIBLE);
            albumRecyclerView.setVisibility(View.GONE);
        }
    }

    private List<AlbumInfo> getAlbumFromMusic(List<MusicFile> musicFiles) {
        Map<String, AlbumInfo> albumMap = new HashMap<>();

        for (MusicFile file : musicFiles) {
            if (file != null) {
                String albumName = file.getAlbumName();
                String artistName = file.getArtistName();
                String albumArt = file.getAlbumArtUri();

                if (albumMap.containsKey(albumName)) {
                    AlbumInfo existingAlbum = albumMap.get(albumName);
                    int currentTrackCount = existingAlbum.getSongCount();
                    existingAlbum.setSongCount(currentTrackCount + 1);
                } else {
                    int songCount = 0;
                    for (AlbumInfo albumInfo : albumMap.values()) {
                        songCount = albumInfo.getSongCount();
                        albumInfo.setSongCount(songCount);
                    }
                    AlbumInfo albumInfo = new AlbumInfo(albumName, artistName, albumArt, songCount);
                    albumMap.put(albumName, albumInfo);
                }
            }
        }

        return new ArrayList<>(albumMap.values());
    }
}
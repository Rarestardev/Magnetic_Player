package com.rarestardev.magneticplayer.listener;

import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.entities.PlaylistEntity;

public interface DeletePlaylistListener {

    void onDeletePlaylist(PlaylistEntity playlist);
    void getPlaylistItemId(String playlistName,RecyclerView recyclerView);
}

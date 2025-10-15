package com.rarestardev.magneticplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.database.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.listener.DeletePlaylistListener;

import java.util.List;

public class PlaylistWithMusicAdapter extends RecyclerView.Adapter<PlaylistWithMusicAdapter.PlaylistWithMusicViewHolder> {

    private List<PlaylistEntity> playlist;
    private final Context context;
    private DeletePlaylistListener listener;

    public PlaylistWithMusicAdapter(Context context) {
        this.context = context;
    }

    public void setPlaylist(List<PlaylistEntity> playlist) {
        this.playlist = playlist;
    }

    public void setListener(DeletePlaylistListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistWithMusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist_with_music, viewGroup, false);
        return new PlaylistWithMusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistWithMusicViewHolder holder, int i) {
        holder.playlistName.setText(playlist.get(i).getPlaylistName());

        holder.item_music_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        holder.item_music_recycler_view.setHasFixedSize(true);

        holder.itemView.setOnClickListener(v -> {
            if (holder.item_music_recycler_view.getVisibility() == View.GONE) {
                holder.item_music_recycler_view.setVisibility(View.VISIBLE);
                holder.forward_ic.setRotation(90f);

                listener.getPlaylistItemId(playlist.get(i).getPlaylistName(), holder.item_music_recycler_view);
            } else {
                holder.item_music_recycler_view.setVisibility(View.GONE);
                holder.forward_ic.setRotation(0);
            }
        });

        holder.delete_playlist.setOnClickListener(v -> listener.onDeletePlaylist(playlist.get(i)));
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }


    public static class PlaylistWithMusicViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView playlistName;
        AppCompatImageView forward_ic, delete_playlist;
        RecyclerView item_music_recycler_view;

        public PlaylistWithMusicViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistName = itemView.findViewById(R.id.playlistName);
            forward_ic = itemView.findViewById(R.id.forward_ic);
            delete_playlist = itemView.findViewById(R.id.delete_playlist);
            item_music_recycler_view = itemView.findViewById(R.id.item_music_recycler_view);
        }
    }
}

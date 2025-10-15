package com.rarestardev.magneticplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.listener.PlaylistListener;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>{

    private final Context context;
    private List<PlaylistEntity> playlist;
    private PlaylistListener listener;

    public PlaylistAdapter(Context context) {
        this.context = context;
    }

    public void setPlaylist(List<PlaylistEntity> playlist) {
        this.playlist = playlist;
    }

    public void setListener(PlaylistListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.play_list_item,viewGroup,false);
        return new PlaylistViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int i) {
        holder.playlistName.setText(playlist.get(i).getPlaylistName());
        holder.itemView.setOnClickListener(v ->
                listener.onClickPlaylistTarget(playlist.get(i).getPlaylistName(),playlist.get(i).getPlaylistId()));
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }


    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{

        AppCompatTextView playlistName;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistName = itemView.findViewById(R.id.playlistName);
        }
    }

}

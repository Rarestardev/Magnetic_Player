package com.rarestardev.magneticplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.controller.MusicPlayerService;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.ArrayList;
import java.util.List;

public class PopularMusicAdapter extends RecyclerView.Adapter<PopularMusicAdapter.PopularMusicViewHolder> {

    private final Context context;
    private List<MusicFile> musicFiles;
    private int listValue;

    public PopularMusicAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<MusicFile> musicFiles, int listValue) {
        this.musicFiles = musicFiles;
        this.listValue = listValue;
    }

    @NonNull
    @Override
    public PopularMusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular_item, viewGroup, false);
        return new PopularMusicViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull PopularMusicViewHolder holder, int i) {
        MusicFile musicFile = musicFiles.get(i);

        Glide.with(context)
                .load(musicFile.getAlbumArtUri())
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .into(holder.cover_music);

        holder.artistName.setText(musicFile.getArtistName());
        holder.music_name.setText(musicFile.getSongTitle());

        holder.itemView.setOnClickListener(v -> startMusicService(musicFiles, i));
    }

    @Override
    public int getItemCount() {
        return Math.min(musicFiles.size(), listValue);
    }

    private void startMusicService(List<MusicFile> musicFiles, int current_position) {
        Intent service = new Intent(context, MusicPlayerService.class);
        service.putParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue(), new ArrayList<>(musicFiles));
        service.putExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), current_position);
        context.startService(service);
    }


    public static class PopularMusicViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView music_name, artistName;
        RoundedImageView cover_music;

        public PopularMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            music_name = itemView.findViewById(R.id.music_name);
            artistName = itemView.findViewById(R.id.artistName);
            cover_music = itemView.findViewById(R.id.cover_music);
        }
    }
}

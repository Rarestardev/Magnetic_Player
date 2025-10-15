package com.rarestardev.magneticplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.controller.MusicPlayerService;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.helper.MusicFileAdapterHelper;
import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.ArrayList;
import java.util.List;

import eu.gsottbauer.equalizerview.EqualizerView;

public class MusicFileAdapter extends RecyclerView.Adapter<MusicFileAdapter.MusicViewHolder> {

    private final Context context;
    private String FilePath;
    private List<MusicFile> musicFiles;
    private int listValue;

    public MusicFileAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<MusicFile> musicFiles, int listValue) {
        this.musicFiles = musicFiles;
        this.listValue = listValue;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music, viewGroup, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int i) {
        MusicFile musicFile = musicFiles.get(i);

        holder.tvFileName.setText(musicFile.getSongTitle());
        holder.tvAlbumName.setText(String.format("%s | %s", musicFile.getArtistName(), musicFile.getAlbumName()));

        Glide.with(context)
                .load(musicFile.getAlbumArtUri())
                .placeholder(R.drawable.ic_music)
                .thumbnail(0.1f)
                .into(holder.albumArt);

        holder.itemView.setOnClickListener(v -> startMusicService(musicFiles, i));
        holder.option_menu.setOnClickListener(v -> {
            MusicFileAdapterHelper adapterHelper = new MusicFileAdapterHelper((context));
            adapterHelper.showOptionMenu(musicFiles, i);
        });

        holder.is_playing_music.setVisibility(View.GONE);

        if (FilePath != null) {
            if (FilePath.equals(musicFile.getFilePath())) {
                holder.is_playing_music.setVisibility(View.VISIBLE);
                holder.is_playing_music.animateBars();
            } else {
                holder.is_playing_music.setVisibility(View.GONE);
                holder.is_playing_music.stopBars();
            }
        } else {
            holder.is_playing_music.setVisibility(View.GONE);
            holder.is_playing_music.stopBars();
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(musicFiles.size(), listValue);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getMusicIsPlaying(String FilePath) {
        this.FilePath = FilePath;
        notifyDataSetChanged();
    }

    private void startMusicService(List<MusicFile> musicFiles, int current_position) {
        Intent service = new Intent(context, MusicPlayerService.class);
        service.putParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue(), new ArrayList<>(musicFiles));
        service.putExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), current_position);
        context.startService(service);
    }


    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView albumArt;
        AppCompatTextView tvFileName, tvAlbumName;
        AppCompatImageView option_menu;
        EqualizerView is_playing_music;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            albumArt = itemView.findViewById(R.id.albumArt);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
            is_playing_music = itemView.findViewById(R.id.is_playing_music);
            option_menu = itemView.findViewById(R.id.option_menu);
        }
    }
}

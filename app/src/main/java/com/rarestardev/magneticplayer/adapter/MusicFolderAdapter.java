package com.rarestardev.magneticplayer.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.listener.FolderClickListener;
import com.rarestardev.magneticplayer.model.MusicFolder;

import java.util.List;

public class MusicFolderAdapter extends RecyclerView.Adapter<MusicFolderAdapter.MusicFolderViewHolder> {

    private final FolderClickListener listener;
    private final List<MusicFolder> musicFolders;

    public MusicFolderAdapter(List<MusicFolder> musicFolders, FolderClickListener listener) {
        this.musicFolders = musicFolders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicFolderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_folder_item, viewGroup, false);
        return new MusicFolderViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MusicFolderViewHolder holder, int i) {
        MusicFolder musicFolder = musicFolders.get(i);
        holder.folder_name.setText(musicFolder.getFolderName());

        holder.itemView.setOnClickListener(v ->
                listener.onClickFolder(musicFolder.getFolderName(), musicFolder.getFolderPath()));
    }

    @Override
    public int getItemCount() {
        return musicFolders.size();
    }


    public static class MusicFolderViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView folder_name;

        public MusicFolderViewHolder(@NonNull View itemView) {
            super(itemView);

            folder_name = itemView.findViewById(R.id.folder_name);
        }
    }
}

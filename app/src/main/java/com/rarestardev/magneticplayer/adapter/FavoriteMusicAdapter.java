package com.rarestardev.magneticplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.List;

public class FavoriteMusicAdapter extends RecyclerView.Adapter<FavoriteMusicAdapter.FavoriteViewHolder> {

    private final Context context;
    private OnFavoriteMusicPlayListener listener;
    private String filePath;
    private List<MusicFile> musicFiles;

    public FavoriteMusicAdapter(Context context) {
        this.context = context;
    }

    public void FavoriteClickListener(OnFavoriteMusicPlayListener listener){
        this.listener = listener;
    }

    public void setList(List<MusicFile> musicFiles) {
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite, viewGroup, false);
        return new FavoriteViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int i) {
        MusicFile musicFile = musicFiles.get(i);

        holder.tvFileName.setText(musicFile.getSongTitle());
        holder.tvAlbumName.setText(musicFile.getArtistName() + " | " + musicFile.getAlbumName());

        Glide.with(context)
                .load(musicFile.getAlbumArtUri())
                .placeholder(R.drawable.ic_music)
                .into(holder.albumArt);

        holder.itemView.setOnClickListener(v -> listener.onMusicPlay(musicFiles, i));

        holder.delete_favorite.setOnClickListener(v -> listener.onDeleteFavorite(musicFile));

        holder.is_playing_music.setVisibility(View.GONE);

        if (filePath != null) {
            if (!filePath.isEmpty() && filePath.equals(musicFile.getFilePath())) {
                holder.is_playing_music.setVisibility(View.VISIBLE);
            } else {
                holder.is_playing_music.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getMusicIsPlaying(String filePath) {
        this.filePath = filePath;
        notifyDataSetChanged();
    }


    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView albumArt;
        AppCompatTextView tvFileName, tvAlbumName;
        AppCompatImageView is_playing_music,delete_favorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            albumArt = itemView.findViewById(R.id.albumArt);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
            is_playing_music = itemView.findViewById(R.id.is_playing_music);
            delete_favorite = itemView.findViewById(R.id.delete_favorite);
        }
    }

    public interface OnFavoriteMusicPlayListener {
        void onMusicPlay(List<MusicFile> musicFiles, int position);

        void onDeleteFavorite(MusicFile musicFile);
    }
}

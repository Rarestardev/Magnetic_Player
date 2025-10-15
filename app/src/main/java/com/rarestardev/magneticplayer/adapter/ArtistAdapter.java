package com.rarestardev.magneticplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.listener.ArtistClickListener;
import com.rarestardev.magneticplayer.model.ArtistMusicModel;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final List<ArtistMusicModel> artistMusicModels;
    private final Context context;
    private ArtistClickListener listener;

    public ArtistAdapter(List<ArtistMusicModel> artistMusicModels, Context context) {
        this.artistMusicModels = artistMusicModels;
        this.context = context;
    }

    public void setListener(ArtistClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_items, viewGroup, false);
        return new ArtistViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int i) {
        ArtistMusicModel artistMusicModel = artistMusicModels.get(i);

        holder.artist_name.setText(artistMusicModel.getArtistName());
        Glide.with(context)
                .load(artistMusicModel.getArtistCover())
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .into(holder.artist_cover);

        holder.itemView.setOnClickListener(v -> listener.onArtistItemClick(artistMusicModel.getArtistName(),artistMusicModel.getArtistCover()));
    }

    @Override
    public int getItemCount() {
        return artistMusicModels.size();
    }


    public static class ArtistViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView artist_cover;
        AppCompatTextView artist_name;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);

            artist_cover = itemView.findViewById(R.id.artist_cover);
            artist_name = itemView.findViewById(R.id.artist_name);
        }
    }
}

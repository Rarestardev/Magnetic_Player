package com.rarestardev.magneticplayer.music_utils.artist;

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
import com.bumptech.glide.request.target.Target;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.LocalArtist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LocalArtist> localArtists;
    private final Context context;
    private ArtistClickListener listener;
    private final int items_size;

    public ArtistAdapter(Context context, int items_size) {
        this.context = context;
        this.items_size = items_size;
    }

    public void artistList(List<LocalArtist> localArtists) {
        this.localArtists = localArtists;
    }

    public void setListener(ArtistClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_items_grid, viewGroup, false);
        return new ArtistViewHolderGrid(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalArtist localArtist = localArtists.get(position);
        if (!localArtists.isEmpty()) {
            int heightsRes = (position % 3 == 0) ? R.dimen.artist_cover_width_grid_large : R.dimen.artist_cover_width_grid;
            int heightPx = (int) context.getResources().getDimension(heightsRes);

            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = heightPx;
            ((ArtistViewHolderGrid) holder).artist_cover.setLayoutParams(params);

            ((ArtistViewHolderGrid) holder).artist_name.setText(localArtist.getArtistName());
            Glide.with(context)
                    .load(localArtist.getArtistCover())
                    .placeholder(context.getDrawable(R.drawable.ic_music))
                    .override(Target.SIZE_ORIGINAL,heightPx)
                    .into(((ArtistViewHolderGrid) holder).artist_cover);

            holder.itemView.setOnClickListener(v ->
                    listener.onArtistItemClick(localArtist.getArtistName(), localArtist.getArtistCover()));
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(items_size, localArtists.size());
    }

    public static class ArtistViewHolderGrid extends RecyclerView.ViewHolder {
        AppCompatImageView artist_cover;
        AppCompatTextView artist_name;

        public ArtistViewHolderGrid(@NonNull View itemView) {
            super(itemView);

            artist_cover = itemView.findViewById(R.id.artist_cover);
            artist_name = itemView.findViewById(R.id.artist_name);
        }
    }

    public interface ArtistClickListener {
        void onArtistItemClick(String artist, String coverPath);
    }
}

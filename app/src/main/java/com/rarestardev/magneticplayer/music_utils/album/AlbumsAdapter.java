package com.rarestardev.magneticplayer.music_utils.album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.listener.OnAlbumClickListener;
import com.rarestardev.magneticplayer.model.AlbumInfo;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolderGridView> {

    private final Context context;
    private List<AlbumInfo> albumInfos;
    private OnAlbumClickListener listener;

    public AlbumsAdapter(Context context) {
        this.context = context;
    }

    public void setAlbumData(List<AlbumInfo> albumInfos) {
        this.albumInfos = albumInfos;
    }

    public void AlbumClickListener(OnAlbumClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumsAdapter.AlbumViewHolderGridView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item_grid_view, viewGroup, false);
        return new AlbumViewHolderGridView(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolderGridView holder, int position) {
        AlbumInfo info = albumInfos.get(position);

        int heightsRes = (position % 3 == 0) ? R.dimen.artist_cover_width_grid_large : R.dimen.artist_cover_width_grid;
        int heightPx = (int) context.getResources().getDimension(heightsRes);

        ViewGroup.LayoutParams params = holder.album_cover.getLayoutParams();
        params.height = heightPx;
        holder.album_cover.setLayoutParams(params);

        holder.artist_name.setText(info.getArtistName());

        holder.album_name.setText(info.getAlbumName());

        Glide.with(context)
                .load(info.getAlbumArtUri())
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .override(Target.SIZE_ORIGINAL, heightPx)
                .into(holder.album_cover);

        holder.itemView.setOnClickListener(v -> listener.onClickAlbum(info));
    }

    @Override
    public int getItemCount() {
        return albumInfos.size();
    }

    public static class AlbumViewHolderGridView extends RecyclerView.ViewHolder {

        RoundedImageView album_cover;
        AppCompatTextView album_name, artist_name;

        public AlbumViewHolderGridView(@NonNull View itemView) {
            super(itemView);

            album_cover = itemView.findViewById(R.id.album_cover);
            album_name = itemView.findViewById(R.id.album_name);
            artist_name = itemView.findViewById(R.id.artist_name);
        }
    }
}

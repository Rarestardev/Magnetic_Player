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

    public void AlbumClickListener(OnAlbumClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolderGridView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_item_grid_view, viewGroup, false);
        return new AlbumViewHolderGridView(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolderGridView holder, int i) {
        AlbumInfo info = albumInfos.get(i);
        holder.album_name.setText(info.getAlbumName());
        holder.artist_name.setText(info.getArtistName());

        Glide.with(context).load(info.getAlbumArtUri()).placeholder(R.drawable.ic_music).into(holder.album_cover);
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

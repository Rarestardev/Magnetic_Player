package com.rarestardev.magneticplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.List;

public class ListQueueAdapter extends RecyclerView.Adapter<ListQueueAdapter.ListQueueViewHolder>{

    private List<MusicFile> musicFiles;
    private final Context context;
    private String songPath;
    private onListQueueListener listener;
    private boolean is_removed_state = false;

    public ListQueueAdapter(Context context) {
        this.context = context;
    }

    public void getListQueue(List<MusicFile> musicFiles){
        this.musicFiles = musicFiles;
    }

    public void setListener(onListQueueListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListQueueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_queue_item,viewGroup,false);
        return new ListQueueViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull ListQueueViewHolder holder, int i) {
        MusicFile musicFile = musicFiles.get(i);

        holder.tvFileName.setText(musicFile.getSongTitle());
        holder.tvAlbumName.setText(musicFile.getArtistName() + " | " + musicFile.getAlbumName());

        Glide.with(context)
                .load(musicFile.getAlbumArtUri())
                .placeholder(R.drawable.ic_music)
                .into(holder.albumArt);


        if (!is_removed_state) {
            holder.itemView.setOnClickListener(v -> {
                listener.onMusicPlay(musicFiles, i);
            });
        } else {
            // Do nothing if in removed state
            holder.itemView.setOnClickListener(v1 -> {
                holder.list_queue_item.setCardBackgroundColor(context.getColor(R.color.second_color_night_mode));
            });
        }

        holder.itemView.setOnLongClickListener(v -> {
            is_removed_state = true;

            return true;
        });

        if (songPath != null){
            if (!songPath.isEmpty() && songPath.equals(musicFile.getFilePath())){
                holder.is_playing_music.setVisibility(View.VISIBLE);
            }else {
                holder.is_playing_music.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getMusicIsPlaying(String songName){
        this.songPath = songName;
        notifyDataSetChanged();
    }


    public static class ListQueueViewHolder extends RecyclerView.ViewHolder {

        CardView list_queue_item;
        RoundedImageView albumArt;
        AppCompatTextView tvFileName,tvAlbumName;
        AppCompatImageView is_playing_music,option_menu;

        public ListQueueViewHolder(@NonNull View itemView) {
            super(itemView);

            list_queue_item = itemView.findViewById(R.id.list_queue_item);
            albumArt = itemView.findViewById(R.id.albumArt);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
            is_playing_music = itemView.findViewById(R.id.is_playing_music);
            option_menu = itemView.findViewById(R.id.option_menu);
        }
    }

    public interface onListQueueListener{
        void onMusicPlay(List<MusicFile> musicFiles, int position);
        void onMultiSelect(List<Integer> items);
    }
}

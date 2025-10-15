package com.rarestardev.magneticplayer.music_utils.genre;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> stringList;
    private final Context context;
    private GenreListener genreListener;

    public GenreAdapter(Context context) {
        this.context = context;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public void setGenreListener(GenreListener genreListener) {
        this.genreListener = genreListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.genre_items_vertical, parent, false);
        return new VerticalGenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String genre = stringList.get(position);

        ((VerticalGenreViewHolder) holder).genre_cover.setImageResource(loadImageWithGenre(genre));
        ((VerticalGenreViewHolder) holder).tv_genre.setText(genre);

        holder.itemView.setOnClickListener(v -> genreListener.onClickGenre(genre));
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
    public static class VerticalGenreViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView genre_cover;
        AppCompatTextView tv_genre;

        public VerticalGenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genre_cover = itemView.findViewById(R.id.genre_cover);
            tv_genre = itemView.findViewById(R.id.tv_genre);
        }
    }

    public interface GenreListener {
        void onClickGenre(String genre);
    }

    public static int loadImageWithGenre(String genre) {
        int image = R.drawable.pop;
        switch (genre) {
            case "Pop":
                image = R.drawable.pop;
                break;
            case "Rock":
                image = R.drawable.rock;
                break;
            case "Jazz":
                image = R.drawable.jazz;
                break;
            case "Classical":
                image = R.drawable.classical;
                break;
            case "Hip Hop":
                image = R.drawable.hip_hop;
                break;
            case "Electronic":
                image = R.drawable.electronic;
                break;
            case "Reggae":
                image = R.drawable.reggae;
                break;
            case "Blues":
                image = R.drawable.blues;
                break;
            case "Metal":
                image = R.drawable.metal;
                break;
            case "Folk":
                image = R.drawable.folk;
                break;
            case "R&B":
                image = R.drawable.rnb;
                break;
            case "Ambient":
                image = R.drawable.ambient;
                break;
            case "Soundtrack":
                image = R.drawable.sound_track;
                break;
            case "World":
                image = R.drawable.world;
                break;
            case "Dance":
                image = R.drawable.dance;
                break;
            case "Instrumental":
                image = R.drawable.instrumental;
                break;
            case "Alternative":
                image = R.drawable.alternative;
                break;
            case "Unknown":
                image = R.drawable.unkown;
                break;
        }

        return image;
    }
}

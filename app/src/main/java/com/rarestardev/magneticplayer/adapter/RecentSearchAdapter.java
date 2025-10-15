package com.rarestardev.magneticplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.entities.SearchEntity;

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>{

    private final List<SearchEntity> searchEntities;
    private final OnRecentClickListener listener;

    public RecentSearchAdapter(List<SearchEntity> searchEntities, OnRecentClickListener listener) {
        this.searchEntities = searchEntities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentSearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_search_item,viewGroup,false);
        return new RecentSearchViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecentSearchViewHolder recentSearchViewHolder, int i) {
        recentSearchViewHolder.word.setText(searchEntities.get(i).getWord());
        recentSearchViewHolder.word.setOnClickListener(v -> listener.onClick(searchEntities.get(i).getWord()));
    }

    @Override
    public int getItemCount() {
        return searchEntities.size();
    }


    public static class RecentSearchViewHolder extends RecyclerView.ViewHolder{

        AppCompatTextView word;

        public RecentSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.word);
        }
    }

    public interface OnRecentClickListener{
        void onClick(String word);
    }
}

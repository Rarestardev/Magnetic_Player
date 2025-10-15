package com.rarestardev.magneticplayer.custom_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.PlaylistAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.entities.PlaylistEntity;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.viewmodel.PlayListViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDialogFragment extends DialogFragment {

    private AppCompatImageView close_dialog;
    private RecyclerView recyclerViewPlaylist;
    private AppCompatImageView add_playlist;
    private AppCompatEditText edit_text_name_playlist;
    private AppCompatTextView noPlaylist;
    private CardView new_playlist_layout;
    private AppCompatTextView save_playlist;

    private PlayListViewModel viewModel;
    private PlaylistAdapter adapter;
    private List<MusicFile> musicFiles;

    private static final String ARG_LIST_NAME = "MusicDetail";
    private static final String ARG_LIST_INDEX = "MusicDetailIndex";

    private int list_position;

    public static PlaylistDialogFragment newInstance(List<MusicFile> musicFile, int index) {
        PlaylistDialogFragment fragment = new PlaylistDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LIST_NAME, new ArrayList<>(musicFile));
        args.putInt(ARG_LIST_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,R.style.TransparentDialogFragment);
        if (getArguments() != null) {
            musicFiles = getArguments().getParcelableArrayList(ARG_LIST_NAME);
            list_position = getArguments().getInt(ARG_LIST_INDEX);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_list_custom_dialog, container, false);

        doInitializationViews(view);

        MusicApplication application = (MusicApplication) getActivity().getApplication();
        viewModel = application.getPlayListViewModel();

        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new PlaylistAdapter(getContext());

        MusicFile musicFile = musicFiles.get(list_position);

        close_dialog.setOnClickListener(v -> dismiss());

        viewModel.getAllPlaylist().observe(this, playlistEntities -> {
            if (playlistEntities != null && !playlistEntities.isEmpty()) {
                adapter.setPlaylist(playlistEntities);
                recyclerViewPlaylist.setAdapter(adapter);
                recyclerViewPlaylist.refreshDrawableState();
                recyclerViewPlaylist.setVisibility(View.VISIBLE);
                noPlaylist.setVisibility(View.GONE);

                adapter.setListener((playlist_name, id) -> {
                    boolean music = viewModel.checkMusicInPlaylist(playlist_name, musicFile);
                    if (music) {
                        Toast.makeText(getContext(), "Music in Playlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(Constants.appLog, "checkMusicInPlaylist is null on playlist dialog");
                        viewModel.addedMusicIntoPlaylist(musicFile, playlist_name);
                        dismiss();
                    }
                });

            } else {
                recyclerViewPlaylist.setVisibility(View.GONE);
                noPlaylist.setVisibility(View.VISIBLE);
                Log.e(Constants.appLog, "showCurrentPlaylist on MusicFileHelper null");
            }
        });

        loadPlaylist();

        add_playlist.setOnClickListener(v -> {
            if (new_playlist_layout.getVisibility() == View.GONE) {
                new_playlist_layout.setVisibility(View.VISIBLE);
                saveNewPlaylist(true);
            } else {
                new_playlist_layout.setVisibility(View.GONE);
                saveNewPlaylist(false);
            }
        });

        return view;
    }

    private void saveNewPlaylist(boolean init) {
        if (init){
            edit_text_name_playlist.requestFocus();
            edit_text_name_playlist.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @SuppressLint("CheckResult")
                @Override
                public void afterTextChanged(Editable s) {
                    String query = s.toString();
                    save_playlist.setOnClickListener(v -> {
                        if (query.isEmpty()) {
                            Toast.makeText(getContext(),
                                            "Please type your playlist name!",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            PlaylistEntity playlist = new PlaylistEntity();
                            playlist.setPlaylistName(query);
                            playlist.setCurrent_date(System.currentTimeMillis());

                            viewModel.insertPlaylist(playlist)
                                    .subscribe(() -> {
                                                Toast.makeText(getContext(), "Saved new playlist", Toast.LENGTH_SHORT).show();
                                                loadPlaylist();
                                            },
                                            throwable ->
                                                    Log.e(Constants.appLog, "Error to save new playlist"));

                            edit_text_name_playlist.setText("");
                            new_playlist_layout.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }else {
            edit_text_name_playlist.clearFocus();
            edit_text_name_playlist.setText("");
        }
    }

    private void loadPlaylist() {
        viewModel.loadPlaylist();
    }

    private void doInitializationViews(View view) {
        close_dialog = view.findViewById(R.id.close_dialog);
        recyclerViewPlaylist = view.findViewById(R.id.recyclerViewPlaylist);
        add_playlist = view.findViewById(R.id.add_playlist);
        noPlaylist = view.findViewById(R.id.noPlaylist);
        new_playlist_layout = view.findViewById(R.id.new_playlist_layout);
        save_playlist = view.findViewById(R.id.save_playlist);
        edit_text_name_playlist = view.findViewById(R.id.edit_text_name_playlist);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null){
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setAttributes(params);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        musicFiles.clear();
    }
}

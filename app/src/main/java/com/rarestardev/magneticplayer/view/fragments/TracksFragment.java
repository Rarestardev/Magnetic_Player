package com.rarestardev.magneticplayer.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.helper.PlayMusicWithEqualizer;
import com.rarestardev.magneticplayer.music_utils.music.adapters.MusicFileAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.music_utils.music.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.enums.ShuffleMode;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.settings.storage.CoverAnimationSettingsStorage;
import com.rarestardev.magneticplayer.settings.storage.SortListSettings;
import com.rarestardev.magneticplayer.utilities.EndOfListMarginDecorator;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TracksFragment extends BaseFragment {

    private MaterialButton shuffle_play, sortSong;
    private RecyclerView trackListRecyclerView;
    private AppCompatTextView not_find_tracks;
    private ProgressBar progress_circular;
    private SortListSettings sortListSettings;
    private MusicPlaybackSettings musicPlaybackSettings;
    private MusicFileAdapter adapter;
    private MusicStatusViewModel musicStatusViewModel;

    public TracksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);

        shuffle_play = view.findViewById(R.id.shuffle_play);
        sortSong = view.findViewById(R.id.sortSong);
        trackListRecyclerView = view.findViewById(R.id.trackListRecyclerView);
        not_find_tracks = view.findViewById(R.id.not_find_tracks);
        progress_circular = view.findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.VISIBLE);

        MusicApplication application = (MusicApplication) getActivity().getApplication();
        musicStatusViewModel = application.getMusicViewModel();

        sortListSettings = new SortListSettings(getContext());
        musicPlaybackSettings = new MusicPlaybackSettings(getContext());

        adapter = new MusicFileAdapter(getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        trackListRecyclerView.refreshDrawableState();
        CoverAnimationSettingsStorage storage = new CoverAnimationSettingsStorage(getContext());
        adapter.setRotateAnimationActive(storage.getEnabledAnimation());

        musicStatusViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), integer -> trackListRecyclerView.scrollToPosition(integer + 1));
    }

    @Override
    protected void onMusicDataLoaded(List<MusicFile> musicFiles) {
        trackListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        trackListRecyclerView.addItemDecoration(new EndOfListMarginDecorator());

        if (musicFiles != null || !musicFiles.isEmpty()) {
            setSortOrderOnList(musicFiles);
            switch (sortListSettings.getPrefSortView()) {
                case "NEWEST":
                    Comparator<MusicFile> comparator = Comparator.comparingLong(MusicFile::getDateAdded);
                    musicFiles.sort(comparator.reversed()); // newest
                    break;
                case "OLDEST":
                    musicFiles.sort(Comparator.comparingLong(MusicFile::getDateAdded)); // oldest
                    break;
                case "ALPHA":
                    musicFiles.sort(Comparator.comparing(MusicFile::getSongTitle)); // title
                    break;
            }

            adapter.setList(musicFiles, musicFiles.size());
            trackListRecyclerView.setAdapter(adapter);
            trackListRecyclerView.setVisibility(View.VISIBLE);
            not_find_tracks.setVisibility(View.GONE);
            progress_circular.setVisibility(View.GONE);

            shuffle_play.setEnabled(!musicFiles.isEmpty());

            shuffle_play.setOnClickListener(v -> {
                Random random = new Random();

                int index = random.nextInt(musicFiles.size());
                new PlayMusicWithEqualizer(getContext()).startMusicService(musicFiles, index, true);

                musicPlaybackSettings.setShuffleMode(ShuffleMode.SHUFFLE);
                Toast.makeText(getContext(), R.string.shuffle_play_started, Toast.LENGTH_SHORT).show();
            });

        } else {
            trackListRecyclerView.setVisibility(View.GONE);
            not_find_tracks.setVisibility(View.VISIBLE);
            progress_circular.setVisibility(View.GONE);
        }

        adapter.setMusicStatusViewModel(musicStatusViewModel);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setSortOrderOnList(List<MusicFile> musicFileList) {
        sortSong.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.sort_music_menu, popupMenu.getMenu());
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.newest_menu) {
                    sortListSettings.setPrefSortView(SortListSettings.NEWEST);
                } else if (item.getItemId() == R.id.oldest_menu) {
                    sortListSettings.setPrefSortView(SortListSettings.OLDEST);
                } else if (item.getItemId() == R.id.alpha_menu) {
                    sortListSettings.setPrefSortView(SortListSettings.ALPHA);
                }
                onMusicDataLoaded(musicFileList);
                return true;
            });
        });
    }
}
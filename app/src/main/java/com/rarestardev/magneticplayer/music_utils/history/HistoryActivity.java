package com.rarestardev.magneticplayer.music_utils.history;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.databinding.ActivityHistoryBinding;
import com.rarestardev.magneticplayer.helper.PlayMusicWithEqualizer;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.RecentList;
import com.rarestardev.magneticplayer.utilities.FileFormater;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.RecentListViewModel;

import java.io.File;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    private ActivityHistoryBinding binding;
    private HistoryMusicAdapter adapter;
    private RecentListViewModel viewModel;

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history);

        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        binding.setLoading(true);
        adapter = new HistoryMusicAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewModel = new ViewModelProvider(this).get(RecentListViewModel.class);
        viewModel.getAllRecentLists().observe(this, recentLists -> {
            if (recentLists != null && !recentLists.isEmpty()) {
                adapter.setViewModel(viewModel, musicStatusViewModel);
                adapter.setList(recentLists);
                binding.recyclerView.setAdapter(adapter);
                binding.setLoading(false);
                binding.setNotFoundItemHistory(false);
                binding.recyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.setLoading(false);
                binding.setNotFoundItemHistory(true);
                binding.recyclerView.setVisibility(View.GONE);
            }
        });

        adapter.setHistoryListener(new HistoryMusicAdapter.HistoryListener() {
            @Override
            public void onClick(List<MusicFile> musicFiles, int current_position) {
                PlayMusicWithEqualizer playMusicWithEqualizer = new PlayMusicWithEqualizer(HistoryActivity.this);
                playMusicWithEqualizer.startMusicService(musicFiles,current_position,false);
            }

            @Override
            public boolean onLongClick(RecentList recentList) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(HistoryActivity.this, R.style.Theme_CustomThemeAlertDialog);
                builder.setTitle(getString(R.string.song) + recentList.getMusicFiles().getSongTitle());
                builder.setIcon(R.drawable.baseline_music_note_24);
                builder.setBackground(getDrawable(R.drawable.custom_alert_dialog));
                String[] options = {getString(R.string.delete), getString(R.string.share), getString(R.string.details)};

                builder.setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            deleteHistory(recentList);
                            dialog.dismiss();
                            break;
                        case 1:
                            shareMusicToOtherApp(recentList.getMusicFiles());
                            dialog.dismiss();
                            break;
                        case 2:
                            showDetailsMusicWithAlertDialog(recentList.getMusicFiles());
                            dialog.dismiss();
                            break;
                    }
                });

                builder.show();

                return true;
            }
        });

        binding.backActivity.setOnClickListener(v -> finish());

        binding.tvDeleteAllHistory.setOnClickListener(v -> {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this, R.style.Theme_CustomThemeAlertDialog);
            dialogBuilder.setBackground(getDrawable(R.drawable.custom_alert_dialog));
            dialogBuilder.setTitle(getString(R.string.warning));
            dialogBuilder.setIcon(R.drawable.ic_warning);
            dialogBuilder.setMessage(getString(R.string.are_you_sure_for_delete_all_history));
            dialogBuilder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                viewModel.deleteAllHistory();
                adapter.notifyDataSetChanged();
                binding.recyclerView.refreshDrawableState();
                dialog.dismiss();
            });

            dialogBuilder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            dialogBuilder.show();
        });
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    private void deleteHistory(RecentList recentList) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this, R.style.Theme_CustomThemeAlertDialog);
        dialogBuilder.setBackground(getDrawable(R.drawable.custom_alert_dialog));
        dialogBuilder.setTitle(recentList.getMusicFiles().getSongTitle());
        dialogBuilder.setIcon(R.drawable.ic_warning);
        dialogBuilder.setMessage(getString(R.string.are_you_sure_for_delete_history));
        dialogBuilder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            viewModel.deleteHistoryItem(recentList);
            adapter.notifyDataSetChanged();
            binding.recyclerView.refreshDrawableState();
            dialog.dismiss();
        });

        dialogBuilder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        dialogBuilder.show();
    }

    private void shareMusicToOtherApp(MusicFile musicFile) {
        File file = new File(musicFile.getFilePath());
        Uri musicUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        intent.putExtra(Intent.EXTRA_STREAM, musicUri);
        intent.setType("music/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, musicFile.getFilePath()));
    }

    private void showDetailsMusicWithAlertDialog(MusicFile musicFile) {
        if (musicFile == null) return;

        final String dialog_msg = getString(R.string.song) + musicFile.getSongTitle() + "\n\n" +
                getString(R.string.artist) + " :  " + musicFile.getArtistName() + "\n\n" +
                getString(R.string.album) + " :  " + musicFile.getAlbumName() + "\n\n" +
                getString(R.string.duration) + FileFormater.formatDuration(musicFile.getDuration()) + "\n\n" +
                getString(R.string.path) + musicFile.getFilePath();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(HistoryActivity.this, R.style.Theme_CustomThemeAlertDialog)
                .setMessage(dialog_msg)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> dialog.dismiss())
                .setCancelable(true);

        builder.show();
    }
}
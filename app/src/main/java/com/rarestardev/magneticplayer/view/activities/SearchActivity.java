package com.rarestardev.magneticplayer.view.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.music_utils.music.adapters.MusicFileAdapter;
import com.rarestardev.magneticplayer.adapter.RecentSearchAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.databinding.ActivitySearchBinding;
import com.rarestardev.magneticplayer.database.entities.SearchEntity;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.EndOfListMarginDecorator;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.SearchMusicViewModel;

public class SearchActivity extends BaseActivity implements RecentSearchAdapter.OnRecentClickListener {

    private ActivitySearchBinding binding;
    private SearchMusicViewModel searchMusicViewModel;
    private MusicFileAdapter adapter;
    private RecentSearchAdapter recentSearchAdapter;

    private String saveRecentSearch = "";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        binding.recyclerViewRecent.setLayoutManager(new GridLayoutManager(this, 4));

        adapter = new MusicFileAdapter(SearchActivity.this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addItemDecoration(new EndOfListMarginDecorator());
        binding.recyclerView.setHasFixedSize(true);
        adapter.setMusicStatusViewModel(musicStatusViewModel);

        searchMusicViewModel = new ViewModelProvider(this).get(SearchMusicViewModel.class);

        doInitialization();
        showRecentSearch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.noResult.setVisibility(View.VISIBLE);
        binding.setNoResultText(getString(R.string.type_a_word_for_search_music));
        binding.recyclerView.setVisibility(View.GONE);
        binding.result.setVisibility(View.GONE);


        binding.editTextSearch.post(() -> {
            binding.editTextSearch.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.editTextSearch, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void doInitialization() {
        binding.backActivity.setOnClickListener(v -> {
            finish();
            binding.editTextSearch.clearFocus();
        });

        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    searchMusicViewModel.search(s.toString());
                    updateUi();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    saveRecentSearch = s.toString();
                }
            }
        });

    }

    private void updateUi() {
        searchMusicViewModel.searchResult().observe(this, musicFiles -> {
            if (musicFiles.isEmpty()) {
                binding.recyclerView.setVisibility(View.GONE);
                binding.setNoResultText("No result!");
                binding.noResult.setVisibility(View.VISIBLE);
                binding.result.setVisibility(View.GONE);
            } else {
                adapter.setList(musicFiles, musicFiles.size());
                binding.recyclerView.setAdapter(adapter);
                binding.recyclerView.refreshDrawableState();
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.noResult.setVisibility(View.GONE);
                binding.result.setVisibility(View.VISIBLE);

                MusicApplication application = (MusicApplication) getApplication();
                MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();
                adapter.setMusicStatusViewModel(musicStatusViewModel);
            }
        });
    }

    private void addToRecentSearch(String query) {
        if (!query.isEmpty()) {
            SearchEntity searchEntity = new SearchEntity();
            searchEntity.setWord(query);

            searchMusicViewModel.insertSearchTypes(searchEntity);
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    private void showRecentSearch() {
        searchMusicViewModel.getAllRecentSearch().observe(this, searchEntities -> {
            if (!searchEntities.isEmpty()) {
                binding.recyclerViewRecent.setVisibility(View.VISIBLE);
                binding.deleteHistory.setVisibility(View.VISIBLE);
                binding.history.setVisibility(View.VISIBLE);
                recentSearchAdapter = new RecentSearchAdapter(searchEntities, this);
                binding.recyclerViewRecent.setAdapter(recentSearchAdapter);
                binding.recyclerViewRecent.setHasFixedSize(true);
                binding.recyclerViewRecent.refreshDrawableState();
            } else {
                binding.recyclerViewRecent.setVisibility(View.GONE);
                binding.deleteHistory.setVisibility(View.GONE);
                binding.history.setVisibility(View.GONE);
            }
        });

        binding.deleteHistory.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SearchActivity.this, R.style.Theme_CustomThemeAlertDialog)
                    .setIcon(R.drawable.ic_warning)
                    .setCancelable(false)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.are_you_sure_for_delete_all_history)
                    .setBackground(getDrawable(R.drawable.custom_alert_dialog))
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        searchMusicViewModel.deleteAllRecentSearch();
                        dialog.dismiss();
                        binding.editTextSearch.setText("");
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (saveRecentSearch != null) {
            if (!saveRecentSearch.isEmpty()) {
                addToRecentSearch(saveRecentSearch);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);
    }


    @Override
    public void onClick(String word) {
        binding.editTextSearch.setText(word);
    }
}
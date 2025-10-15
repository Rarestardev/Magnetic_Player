package com.rarestardev.magneticplayer.music_utils.music.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.AdiveryNativeAdView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.service.MusicPlayerService;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.FileFormater;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.io.File;

public class ExpandedMenuMusicPlayerDialog extends Dialog {

    private MusicFile musicFile;
    private final Context context;
    private int page = 1;
    private LinearLayoutCompat page_one, page_two;
    private static final Integer[] TIMES = {10, 20, 30, 40, 50, 60, 70, 80, 90};
    private final String[] displayTimes = new String[TIMES.length];
    private MusicPlayerService service;
    private MusicStatusViewModel musicStatusViewModel;
    private RelativeLayout layout_sleep_timer;
    private AppCompatTextView tv_sleep_timer;
    private AppCompatTextView tv_timer;

    public ExpandedMenuMusicPlayerDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.expanded_menu_dialog);
        this.context = context;
    }

    public void setMusicFile(MusicFile musicFile) {
        this.musicFile = musicFile;
    }

    public void musicService(MusicPlayerService service) {
        this.service = service;
    }

    public void getViewModel(MusicStatusViewModel musicStatusViewModel) {
        this.musicStatusViewModel = musicStatusViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setWindowAnimations(R.style.DialogAnimation);

        MaterialButton btn_detail_music = findViewById(R.id.btn_detail_music);
        MaterialButton btn_show_cover = findViewById(R.id.btn_show_cover);
        MaterialButton btn_share_music = findViewById(R.id.btn_share_music);
        tv_sleep_timer = findViewById(R.id.tv_sleep_timer);
        tv_timer = findViewById(R.id.tv_timer);
        ListView time_list = findViewById(R.id.time_list);
        AppCompatImageView back_to_page_one = findViewById(R.id.back_to_page_one);
        page_one = findViewById(R.id.page_one);
        page_two = findViewById(R.id.page_two);
        layout_sleep_timer = findViewById(R.id.layout_sleep_timer);

        AdiveryBannerAdView bannerAdView = findViewById(R.id.banner_ad);
        AdiveryNativeAdView adView = findViewById(R.id.native_ad_view);

        AdNetworkManager adNetworkManager = new AdNetworkManager(context);
        adNetworkManager.showSmallBannerAds(bannerAdView);
        adNetworkManager.showNativeBannerAd(adView);

        sleepTimerClickable(context.getString(R.string.sleep_timer));

        back_to_page_one.setOnClickListener(v -> {
            page = 1;
            updatePage();
        });

        btn_detail_music.setOnClickListener(v -> {
            dismiss();
            final String dialog_msg = "Song :  " + musicFile.getSongTitle() + "\n\n" + "Artist :  " + musicFile.getArtistName() + "\n\n" +
                    "Album :  " + musicFile.getAlbumName() + "\n\n" + "Duration :  " + FileFormater.formatDuration(musicFile.getDuration())
                    + "\n\n" + "Path :  " + musicFile.getFilePath();

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.Theme_CustomThemeAlertDialog)
                    .setMessage(dialog_msg)
                    .setCancelable(true)
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

            builder.show();
        });

        btn_share_music.setOnClickListener(v -> {
            File file = new File(musicFile.getFilePath());
            Uri musicUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, file.getName());
            intent.putExtra(Intent.EXTRA_STREAM, musicUri);
            intent.setType("music/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, musicFile.getFilePath()));
            dismiss();
        });

        btn_show_cover.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            ImageView imageView = new ImageView(context);

            Glide.with(context)
                    .load(musicFile.getAlbumArtUri())
                    .placeholder(R.drawable.ic_music)
                    .into(imageView);

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            builder.setView(imageView);
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
            dismiss();
        });

        for (int i = 0; i < TIMES.length; i++) {
            displayTimes[i] = TIMES[i] + " " + context.getString(R.string.minute);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, displayTimes);
        time_list.setAdapter(adapter);

        time_list.setOnItemClickListener((parent, view, position, id) -> {
            int minutes = TIMES[position];
            long seconds = minutes * 60L * 1000;
            service.sleepTimerMusic(seconds);
            page = 1;
            updatePage();
            Toast.makeText(context, R.string.sleep_timer_started, Toast.LENGTH_SHORT).show();
        });

        musicStatusViewModel.getTimerMillis().observe((LifecycleOwner) context, aLong -> {
            if (aLong != 0L) {
                tv_timer.setVisibility(View.VISIBLE);
                tv_timer.setText(formatTime(aLong));
                sleepTimerClickable(context.getString(R.string.stop_timer));
            } else {
                sleepTimerClickable(context.getString(R.string.sleep_timer));
            }
        });
    }

    private void sleepTimerClickable(String name) {
        tv_sleep_timer.setText(name);

        if (name.equals(context.getString(R.string.sleep_timer))) {
            layout_sleep_timer.setOnClickListener(v -> {
                page = 2;
                updatePage();
            });
        } else if (name.equals(context.getString(R.string.stop_timer))) {
            layout_sleep_timer.setOnClickListener(v -> {
                service.stopSleepTimer();
                tv_timer.setVisibility(View.GONE);
                sleepTimerClickable(context.getString(R.string.sleep_timer));
            });
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private void updatePage() {
        switch (page) {
            case 1:
                page_one.setVisibility(View.VISIBLE);
                page_two.setVisibility(View.GONE);
                break;
            case 2:
                page_one.setVisibility(View.GONE);
                page_two.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (page == 2) {
            page = 1;
            updatePage();
        } else if (page == 1) {
            dismiss();
        }
    }
}

package com.rarestardev.magneticplayer.custom_views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.AdiveryNativeAdView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.FileFormater;

import java.io.File;

public class ExpandedMenuMusicPlayerDialog extends Dialog {

    private MusicFile musicFile;
    private final Context context;

    public ExpandedMenuMusicPlayerDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.expanded_menu_dialog);
        this.context = context;
    }

    public void setMusicFile(MusicFile musicFile) {
        this.musicFile = musicFile;
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

        AdiveryBannerAdView bannerAdView = findViewById(R.id.banner_ad);
        AdiveryNativeAdView adView = findViewById(R.id.native_ad_view);

        AdNetworkManager adNetworkManager = new AdNetworkManager(context);
        adNetworkManager.showSmallBannerAds(bannerAdView);
        adNetworkManager.showNativeBannerAd(adView);

        btn_detail_music.setOnClickListener(v -> {
            dismiss();
            final String dialog_msg = "Song :  " + musicFile.getSongTitle() + "\n\n" + "Artist :  " + musicFile.getArtistName() + "\n\n" +
                    "Album :  " + musicFile.getAlbumName() + "\n\n" + "Duration :  " + FileFormater.formatDuration(musicFile.getDuration())
                    + "\n\n" + "Path :  " + musicFile.getFilePath();

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context,R.style.Theme_CustomThemeAlertDialog)
                    .setMessage(dialog_msg)
                    .setCancelable(true)
                    .setPositiveButton("Close",(dialog, which) -> dialog.dismiss());

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
    }
}

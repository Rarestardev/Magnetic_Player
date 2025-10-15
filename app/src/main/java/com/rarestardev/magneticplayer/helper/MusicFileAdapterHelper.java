package com.rarestardev.magneticplayer.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.AdiveryNativeAdView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.custom_views.PlaylistDialogFragment;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.FileFormater;
import com.rarestardev.magneticplayer.viewmodel.FavoriteMusicViewModel;

import java.io.File;
import java.util.List;

public class MusicFileAdapterHelper {

    private final Context context;
    private final FavoriteMusicViewModel favoriteMusicViewModel;
    private static final int[] FAVORITE = {R.string.remove_on_favorite, R.string.add_to_favorite};

    public MusicFileAdapterHelper(Context context) {
        this.context = context;
        favoriteMusicViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(FavoriteMusicViewModel.class);
    }

    @SuppressLint("SetTextI18n")
    public void showOptionMenu(List<MusicFile> musicFiles, int index) {
        if (musicFiles == null) {
            return;
        }

        Dialog dialog = new Dialog(context);
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_option_menu_dialog);
        dialog.show();

        MusicFile musicFile = musicFiles.get(index);

        AppCompatTextView tvFileName = dialog.findViewById(R.id.tvFileName);
        RelativeLayout favoriteLayout = dialog.findViewById(R.id.favoriteLayout);
        AppCompatImageView favoriteIcon = dialog.findViewById(R.id.favoriteIcon);
        AppCompatTextView favoriteTextView = dialog.findViewById(R.id.favoriteTextView);
        AppCompatTextView addPlaylistTextView = dialog.findViewById(R.id.addPlaylistTextView);
        AppCompatTextView shareTextView = dialog.findViewById(R.id.shareTextView);
        AppCompatTextView detailMusicTextView = dialog.findViewById(R.id.detailMusicTextView);
        AdiveryBannerAdView bannerAdView = dialog.findViewById(R.id.banner_ad);
        AdiveryNativeAdView adView = dialog.findViewById(R.id.native_ad_view);

        AdNetworkManager adNetworkManager = new AdNetworkManager(context);
        adNetworkManager.showSmallBannerAds(bannerAdView);
        adNetworkManager.showNativeBannerAd(adView);

        tvFileName.setText(String.format("%s _ %s", musicFile.getArtistName(), musicFile.getSongTitle()));

        favoriteMusicViewModel.getFilePath(musicFile.getFilePath()).observe((LifecycleOwner) context, s -> {
            if (s != null) {
                if (s.equals(musicFile.getFilePath())) {
                    favoriteIcon.setImageResource(R.drawable.ic_favorite);
                    favoriteTextView.setText(context.getString(FAVORITE[0]));

                    favoriteLayout.setOnClickListener(v -> {
                        favoriteMusicViewModel.deleteFavoriteMusic(musicFile);
                        dialog.dismiss();
                    });

                } else {
                    favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                    favoriteTextView.setText(context.getString(FAVORITE[1]));

                    favoriteLayout.setOnClickListener(v -> {
                        favoriteMusicViewModel.insertFavoriteData(musicFile);
                        dialog.dismiss();
                    });
                }

            } else {
                favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                favoriteTextView.setText(context.getString(FAVORITE[1]));
                favoriteLayout.setOnClickListener(v -> {
                    favoriteMusicViewModel.insertFavoriteData(musicFile);
                    dialog.dismiss();
                });
            }

        });

        addPlaylistTextView.setOnClickListener(v -> {
            dialog.dismiss();
            if (context instanceof AppCompatActivity) {
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                PlaylistDialogFragment mFragment = PlaylistDialogFragment.newInstance(musicFiles, index);
                mFragment.show(fragmentManager, "PlaylistDialog");
            }
        });

        shareTextView.setOnClickListener(v -> {
            shareMusicToOtherApp(musicFile);
            dialog.dismiss();
        });

        detailMusicTextView.setOnClickListener(v -> {
            dialog.dismiss();
            showDetailsMusicWithAlertDialog(musicFile);
        });
    }

    private void shareMusicToOtherApp(MusicFile musicFile) {
        File file = new File(musicFile.getFilePath());
        Uri musicUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        intent.putExtra(Intent.EXTRA_STREAM, musicUri);
        intent.setType("music/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, musicFile.getFilePath()));
    }

    private void showDetailsMusicWithAlertDialog(MusicFile musicFile) {
        if (musicFile == null) return;

        final String dialog_msg = "Song :  " + musicFile.getSongTitle() + "\n\n" + "Artist :  " + musicFile.getArtistName() + "\n\n" +
                "Album :  " + musicFile.getAlbumName() + "\n\n" + "Duration :  " + FileFormater.formatDuration(musicFile.getDuration())
                + "\n\n" + "Path :  " + musicFile.getFilePath();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.Theme_CustomThemeAlertDialog)
                .setMessage(dialog_msg)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true);

        builder.show();
    }
}

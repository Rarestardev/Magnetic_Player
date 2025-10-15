package com.rarestardev.magneticplayer.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.adivery.sdk.Adivery;
import com.adivery.sdk.AdiveryAdListener;
import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.AdiveryNativeAdView;

/**
 * this class for management ads banner and check the internet connection
 * for handle showing ads.
 *
 * @author rarestar.dev
 * @apiNote Adivery.com
 */
public class AdNetworkManager {

    private final Context context;
    private static final String PLACEMENT_ID_BANNER = "e5f96701-389e-4862-b550-ec59bc39e03d";
    private static final String PLACEMENT_ID_NATIVE = "6fb89f6b-6d4c-46fe-af06-47849426eeeb";
    public AdNetworkManager(Context context) {
        this.context = context;
    }

    public void doInitializationAds() {
        Adivery.configure(((Activity) context).getApplication(), Constants.AD_APP_ID);
        Adivery.setLoggingEnabled(true);
    }

    public void showNativeBannerAd(AdiveryNativeAdView adView) {
        if (Constants.isShowingAdsInApp && isInternetConnected()) {
            new Thread(() -> {
                adView.setListener(new AdiveryAdListener() {
                    @Override
                    public void onAdLoaded() {
                        Log.d(Constants.appLog, "Loaded native ad");
                        adView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(String reason) {
                        Log.e(Constants.appLog, "Native ad on error : " + reason);
                    }

                    @Override
                    public void onAdShown() {
                        adView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdClicked() {

                    }
                });

                new Handler(Looper.getMainLooper()).post(() -> adView.loadAd(PLACEMENT_ID_NATIVE));
            }).start();
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    public void showNativeBannerAdWithHide(AdiveryNativeAdView adView, RelativeLayout layout) {
        if (Constants.isShowingAdsInApp && isInternetConnected()) {
            new Thread(() -> {
                adView.setListener(new AdiveryAdListener() {
                    @Override
                    public void onAdLoaded() {
                        Log.d(Constants.appLog, "Loaded native ad");
                        layout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(String reason) {
                        Log.e(Constants.appLog, "Native ad on error : " + reason);
                        layout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdShown() {
                        layout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdClicked() {
                        layout.setVisibility(View.GONE);
                    }
                });

                new Handler(Looper.getMainLooper()).post(() -> adView.loadAd(PLACEMENT_ID_NATIVE));
            }).start();
        } else {
            layout.setVisibility(View.GONE);
            Log.i(Constants.appLog, "disable ad");
        }
    }

    public void showSmallBannerAds(AdiveryBannerAdView targetBanner) {
        if (Constants.isShowingAdsInApp && isInternetConnected()) {
            targetBanner.setVisibility(View.GONE);
            new Thread(() -> {
                targetBanner.setBannerAdListener(new AdiveryAdListener() {
                    @Override
                    public void onAdLoaded() {
                        targetBanner.setVisibility(View.VISIBLE);
                        Log.d(Constants.appLog, "banner is loaded");
                    }

                    @Override
                    public void onError(String reason) {
                        Log.e(Constants.appLog, reason);
                        targetBanner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdClicked() {

                    }
                });

                Adivery.setLoggingEnabled(true);

                new Handler(Looper.getMainLooper()).post(() -> {
                    targetBanner.setPlacementId(PLACEMENT_ID_BANNER);
                    targetBanner.setRetryOnError(true);
                    targetBanner.loadAd();
                });
            }).start();
        } else {
            targetBanner.setVisibility(View.GONE);
        }
    }

    // check internet connection
    private Boolean isInternetConnected() {
        if (isInternetEnable()) {
            try {
                Process process = Runtime.getRuntime().exec("ping -c 1 google.com");
                int returnVal = process.waitFor();
                return (returnVal == 0);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(Constants.appLog, e.getMessage());
            }
        }
        return false;
    }

    // Check enable wifi or mobile data
    private Boolean isInternetEnable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}

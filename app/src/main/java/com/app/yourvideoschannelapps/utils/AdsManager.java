package com.app.yourvideoschannelapps.utils;

import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.IRONSOURCE;

import android.app.Activity;
import android.view.View;

import com.app.yourvideoschannelapps.BuildConfig;
import com.app.yourvideoschannelapps.R;
import com.app.yourvideoschannelapps.config.AppConfig;
import com.app.yourvideoschannelapps.databases.prefs.AdsPref;
import com.app.yourvideoschannelapps.databases.prefs.SharedPref;
import com.solodroid.ads.sdk.format.AdNetwork;
import com.solodroid.ads.sdk.format.AppOpenAd;
import com.solodroid.ads.sdk.format.BannerAd;
import com.solodroid.ads.sdk.format.InterstitialAd;
import com.solodroid.ads.sdk.format.NativeAd;
import com.solodroid.ads.sdk.format.NativeAdView;
import com.solodroid.ads.sdk.gdpr.GDPR;
import com.solodroid.ads.sdk.gdpr.LegacyGDPR;
import com.solodroid.ads.sdk.util.OnShowAdCompleteListener;

public class AdsManager {

    Activity activity;
    AdNetwork.Initialize adNetwork;
    AppOpenAd.Builder appOpenAd;
    BannerAd.Builder bannerAd;
    InterstitialAd.Builder interstitialAd;
    NativeAd.Builder nativeAd;
    NativeAdView.Builder nativeAdView;
    SharedPref sharedPref;
    AdsPref adsPref;
    LegacyGDPR legacyGDPR;
    GDPR gdpr;

    public AdsManager(Activity activity) {
        this.activity = activity;
        this.sharedPref = new SharedPref(activity);
        this.adsPref = new AdsPref(activity);
        this.legacyGDPR = new LegacyGDPR(activity);
        this.gdpr = new GDPR(activity);
        adNetwork = new AdNetwork.Initialize(activity);
        bannerAd = new BannerAd.Builder(activity);
        appOpenAd = new AppOpenAd.Builder(activity);
        interstitialAd = new InterstitialAd.Builder(activity);
        nativeAd = new NativeAd.Builder(activity);
        nativeAdView = new NativeAdView.Builder(activity);
    }

    public void initializeAd() {
        adNetwork.setAdStatus(adsPref.getAdStatus())
                .setAdNetwork(adsPref.getMainAds())
                .setBackupAdNetwork(adsPref.getBackupAds())
                .setStartappAppId(adsPref.getStartappAppId())
                .setUnityGameId(adsPref.getUnityGameId())
                .setIronSourceAppKey(adsPref.getIronSourceAppKey())
                .setWortiseAppId(adsPref.getWortiseAppId())
                .setDebug(BuildConfig.DEBUG)
                .build();
    }

    public void loadAppOpenAd(boolean placement, OnShowAdCompleteListener onShowAdCompleteListener) {
        if (placement) {
            appOpenAd = new AppOpenAd.Builder(activity)
                    .setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobAppOpenId(adsPref.getAdMobAppOpenAdId())
                    .setAdManagerAppOpenId(adsPref.getAdManagerAppOpenAdId())
                    .setApplovinAppOpenId(adsPref.getAppLovinAppOpenAdUnitId())
                    .setWortiseAppOpenId(adsPref.getWortiseAppOpenId())
                    .build(onShowAdCompleteListener);
        } else {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    public void loadAppOpenAd(boolean placement) {
        if (placement) {
            appOpenAd = new AppOpenAd.Builder(activity)
                    .setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobAppOpenId(adsPref.getAdMobAppOpenAdId())
                    .setAdManagerAppOpenId(adsPref.getAdManagerAppOpenAdId())
                    .setApplovinAppOpenId(adsPref.getAppLovinAppOpenAdUnitId())
                    .setWortiseAppOpenId(adsPref.getWortiseAppOpenId())
                    .build();
        }
    }

    public void showAppOpenAd(boolean placement) {
        if (placement) {
            appOpenAd.show();
        }
    }

    public void destroyAppOpenAd(boolean placement) {
        if (placement) {
            appOpenAd.destroyOpenAd();
        }
    }

    public void loadBannerAd(boolean placement) {
        if (placement) {
            bannerAd.setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobBannerId(adsPref.getAdMobBannerId())
                    .setGoogleAdManagerBannerId(adsPref.getAdManagerBannerId())
                    .setFanBannerId(adsPref.getFanBannerId())
                    .setUnityBannerId(adsPref.getUnityBannerPlacementId())
                    .setAppLovinBannerId(adsPref.getAppLovinBannerAdUnitId())
                    .setAppLovinBannerZoneId(adsPref.getAppLovinBannerZoneId())
                    .setIronSourceBannerId(adsPref.getIronSourceBannerId())
                    .setWortiseBannerId(adsPref.getWortiseBannerId())
                    .setDarkTheme(sharedPref.getIsDarkTheme())
                    .setPlacementStatus(1)
                    .build();

        }
    }

    public void loadInterstitialAd(boolean placement, int interval) {
        if (placement) {
            interstitialAd.setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobInterstitialId(adsPref.getAdMobInterstitialId())
                    .setGoogleAdManagerInterstitialId(adsPref.getAdManagerInterstitialId())
                    .setFanInterstitialId(adsPref.getFanInterstitialId())
                    .setUnityInterstitialId(adsPref.getUnityInterstitialPlacementId())
                    .setAppLovinInterstitialId(adsPref.getAppLovinInterstitialAdUnitId())
                    .setAppLovinInterstitialZoneId(adsPref.getAppLovinInterstitialZoneId())
                    .setIronSourceInterstitialId(adsPref.getIronSourceInterstitialId())
                    .setWortiseInterstitialId(adsPref.getWortiseInterstitialId())
                    .setInterval(interval)
                    .setPlacementStatus(1)
                    .build();
        }
    }

    public void loadNativeAd(boolean placement, String nativeAdStyle) {
        if (placement) {
            nativeAd.setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobNativeId(adsPref.getAdMobNativeId())
                    .setAdManagerNativeId(adsPref.getAdManagerNativeId())
                    .setFanNativeId(adsPref.getFanNativeId())
                    .setAppLovinNativeId(adsPref.getAppLovinNativeAdManualUnitId())
                    .setAppLovinDiscoveryMrecZoneId(adsPref.getAppLovinBannerMrecZoneId())
                    .setWortiseNativeId(adsPref.getWortiseNativeId())
                    .setPlacementStatus(1)
                    .setDarkTheme(sharedPref.getIsDarkTheme())
                    .setNativeAdStyle(nativeAdStyle)
                    .setNativeAdBackgroundColor(R.color.color_light_native_ad_background, R.color.color_dark_native_ad_background)
                    .build();
        }
    }

    public void loadNativeAdView(View view, boolean placement) {
        if (placement) {
            nativeAdView.setAdStatus(adsPref.getAdStatus())
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setAdMobNativeId(adsPref.getAdMobNativeId())
                    .setAdManagerNativeId(adsPref.getAdManagerNativeId())
                    .setFanNativeId(adsPref.getFanNativeId())
                    .setAppLovinNativeId(adsPref.getAppLovinNativeAdManualUnitId())
                    .setWortiseNativeId(adsPref.getWortiseNativeId())
                    .setPlacementStatus(1)
                    .setDarkTheme(sharedPref.getIsDarkTheme())
                    .setView(view)
                    .setNativeAdBackgroundColor(R.color.color_light_native_ad_background, R.color.color_dark_native_ad_background)
                    .build();
        }
    }

    public void destroyBannerAd() {
        bannerAd.destroyAndDetachBanner();
    }

    public void resumeBannerAd(boolean placement) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && !adsPref.getIronSourceBannerId().equals("0")) {
            if (adsPref.getMainAds().equals(IRONSOURCE) || adsPref.getBackupAds().equals(IRONSOURCE)) {
                loadBannerAd(placement);
            }
        }
    }

    public void showInterstitialAd() {
        interstitialAd.show();
    }

    public void updateConsentStatus() {
        if (AppConfig.ENABLE_GDPR_UMP_SDK) {
            gdpr.updateGDPRConsentStatus(adsPref.getMainAds(), false, false);
        }
    }

}

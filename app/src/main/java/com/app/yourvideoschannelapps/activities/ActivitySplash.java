package com.app.yourvideoschannelapps.activities;

import static com.app.yourvideoschannelapps.config.AppConfig.DELAY_SPLASH;
import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.yourvideoschannelapps.BuildConfig;
import com.app.yourvideoschannelapps.R;
import com.app.yourvideoschannelapps.a.LogUtils;
import com.app.yourvideoschannelapps.callbacks.CallbackSettings;
import com.app.yourvideoschannelapps.config.AppConfig;
import com.app.yourvideoschannelapps.databases.prefs.AdsPref;
import com.app.yourvideoschannelapps.databases.prefs.SharedPref;
import com.app.yourvideoschannelapps.models.Ads;
import com.app.yourvideoschannelapps.models.App;
import com.app.yourvideoschannelapps.models.Placement;
import com.app.yourvideoschannelapps.models.Settings;
import com.app.yourvideoschannelapps.rests.RestAdapter;
import com.app.yourvideoschannelapps.utils.AdsManager;
import com.app.yourvideoschannelapps.utils.Constant;
import com.app.yourvideoschannelapps.utils.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    public static final String TAG = "ActivitySplash";
    ProgressBar progressBar;
    ImageView img_splash;
    SharedPref sharedPref;
    AdsPref adsPref;
    AdsManager adsManager;
    Settings settings;
    App app;
    Ads ads;
    Placement adsPlacement;
    Call<CallbackSettings> callbackCall = null;
    boolean isForceOpenAds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_splash);
        Tools.setNavigation(this);

        isForceOpenAds = AppConfig.FORCE_TO_SHOW_APP_OPEN_AD_ON_START;

        adsManager = new AdsManager(this);
        adsManager.initializeAd();

        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);

        img_splash = findViewById(R.id.img_splash);
        if (sharedPref.getIsDarkTheme()) {
            img_splash.setImageResource(R.drawable.bg_splash_dark);
        } else {
            img_splash.setImageResource(R.drawable.bg_splash_default);
        }

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (AppConfig.ALLOW_APP_ACCESSED_USING_VPN) {
            Tools.postDelayed(this::requestConfig, DELAY_SPLASH);
        } else {
            if (Tools.isVpnConnectionAvailable()) {
                Tools.showWarningDialog(ActivitySplash.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            } else {
                Tools.postDelayed(this::requestConfig, DELAY_SPLASH);
            }
        }

    }

    @SuppressWarnings("ConstantValue")
    private void requestConfig() {
        if (AppConfig.SERVER_KEY.contains("XXXXX")) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("App not configured")
                    .setMessage("Please put your Server Key and Rest API Key from settings menu in your admin panel to AppConfig, you can see the documentation for more detailed instructions.")
                    .setPositiveButton(getString(R.string.dialog_ok), (dialogInterface, i) -> startMainActivity())
                    .setCancelable(false)
                    .show();
        } else {
            String data = Tools.decode(AppConfig.SERVER_KEY);
            Log.i("log","data:"+data);
            String[] results = data.split("_applicationId_");
            String baseUrl = results[0].replace("localhost", Constant.LOCALHOST_ADDRESS);
            String applicationId = results[1];
            Log.i("log","baseUrl:"+baseUrl);
            sharedPref.setBaseUrl(baseUrl);

            if (applicationId.equals(BuildConfig.APPLICATION_ID)) {
                if (Tools.isConnect(this)) {
                    requestAds(baseUrl);
                } else {
                    startMainActivity();
                }
            } else {
                String message = "applicationId does not match, applicationId in your app is : " + BuildConfig.APPLICATION_ID +
                        "\n\n But your Server Key is registered with applicationId : " + applicationId + "\n\n" +
                        "Please update your Server Key with the appropriate registration applicationId that is used in your Android project.";
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage(Html.fromHtml(message))
                        .setPositiveButton(getString(R.string.dialog_ok), (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            }
        }
    }

    private void requestAds(String apiUrl) {
        this.callbackCall = RestAdapter.createAPI(apiUrl).getConfig(BuildConfig.APPLICATION_ID, AppConfig.REST_API_KEY);
        this.callbackCall.enqueue(new Callback<CallbackSettings>() {
            public void onResponse(@NonNull Call<CallbackSettings> call, @NonNull Response<CallbackSettings> response) {
                CallbackSettings resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    settings = resp.settings;
                    app = resp.app;
                    ads = resp.ads;
                    adsPlacement = resp.ads_placement;

                    adsPref.saveAds(
                            ads.ad_status.replace("on", "1"),
                            ads.ad_type,
                            ads.backup_ads,
                            ads.admob_publisher_id,
                            ads.admob_app_id,
                            ads.admob_banner_unit_id,
                            ads.admob_interstitial_unit_id,
                            ads.admob_native_unit_id,
                            ads.admob_app_open_ad_unit_id,
                            ads.ad_manager_banner_unit_id,
                            ads.ad_manager_interstitial_unit_id,
                            ads.ad_manager_native_unit_id,
                            ads.ad_manager_app_open_ad_unit_id,
                            ads.fan_banner_unit_id,
                            ads.fan_interstitial_unit_id,
                            ads.fan_native_unit_id,
                            ads.startapp_app_id,
                            ads.unity_game_id,
                            ads.unity_banner_placement_id,
                            ads.unity_interstitial_placement_id,
                            ads.applovin_banner_ad_unit_id,
                            ads.applovin_interstitial_ad_unit_id,
                            ads.applovin_native_ad_manual_unit_id,
                            ads.applovin_app_open_ad_unit_id,
                            ads.applovin_banner_zone_id,
                            ads.applovin_banner_mrec_zone_id,
                            ads.applovin_interstitial_zone_id,
                            ads.ironsource_app_key,
                            ads.ironsource_banner_placement_name,
                            ads.ironsource_interstitial_placement_name,
                            ads.wortise_app_id,
                            ads.wortise_app_open_unit_id,
                            ads.wortise_banner_unit_id,
                            ads.wortise_interstitial_unit_id,
                            ads.wortise_native_unit_id,
                            ads.interstitial_ad_interval,
                            ads.native_ad_interval,
                            ads.native_ad_index
                    );

                    adsPref.setPlacement(
                            adsPlacement.banner_home == 1,
                            adsPlacement.banner_post_details == 1,
                            adsPlacement.banner_category_details == 1,
                            adsPlacement.banner_search == 1,
                            adsPlacement.interstitial_post_list == 1,
                            adsPlacement.interstitial_post_details == 1,
                            adsPlacement.native_ad_post_list == 1,
                            adsPlacement.native_ad_post_details == 1,
                            adsPlacement.native_ad_exit_dialog == 1,
                            adsPlacement.app_open_ad_on_start == 1,
                            adsPlacement.app_open_ad_on_resume == 1
                    );

                    sharedPref.saveConfig(
                            resp.key,
                            settings.more_apps_url,
                            settings.privacy_policy,
                            app.redirect_url,
                            settings.providers
                    );

                    if (app.status.equals("0")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityRedirect.class);
                        intent.putExtra("redirect_url", app.redirect_url);
                        startActivity(intent);
                        finish();
                        Log.d(TAG, "App status is suspended");
                    } else {
                        showAppOpenAdIfAvailable();
                        Log.d(TAG, "Ads data is saved");
                    }

                } else {
                    showAppOpenAdIfAvailable();
                }
            }

            public void onFailure(@NonNull Call<CallbackSettings> call, @NonNull Throwable th) {
                Log.e(TAG, "onFailure " + th.getMessage());
                showAppOpenAdIfAvailable();
            }
        });
    }

    private void showAppOpenAdIfAvailable() {
        if (isForceOpenAds) {
            if (adsPref.getIsOpenAd()) {
                adsManager.loadAppOpenAd(adsPref.getIsAppOpenAdOnStart(), this::startMainActivity);
            } else {
                startMainActivity();
            }
        } else {
            if (adsPref.getAdStatus().equals(AD_STATUS_ON) && adsPref.getIsAppOpenAdOnStart()) {
                switch (adsPref.getMainAds()) {
                    case ADMOB:
                        if (!adsPref.getAdMobAppOpenAdId().equals("0")) {
                            ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                        } else {
                            startMainActivity();
                        }
                        break;
                    case GOOGLE_AD_MANAGER:
                        if (!adsPref.getAdManagerAppOpenAdId().equals("0")) {
                            ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                        } else {
                            startMainActivity();
                        }
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                        if (!adsPref.getAppLovinAppOpenAdUnitId().equals("0")) {
                            ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                        } else {
                            startMainActivity();
                        }
                        break;
                    case WORTISE:
                        if (!adsPref.getWortiseAppOpenId().equals("0")) {
                            ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                        } else {
                            startMainActivity();
                        }
                        break;
                    default:
                        startMainActivity();
                        break;
                }
            } else {
                startMainActivity();
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}

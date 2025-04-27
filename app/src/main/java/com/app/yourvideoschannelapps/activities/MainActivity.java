package com.app.yourvideoschannelapps.activities;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import com.app.yourvideoschannelapps.BuildConfig;
import com.app.yourvideoschannelapps.R;
import com.app.yourvideoschannelapps.a.AdvDialog;
import com.app.yourvideoschannelapps.a.FireEvent;
import com.app.yourvideoschannelapps.a.LogUtils;
import com.app.yourvideoschannelapps.a.Utils;
import com.app.yourvideoschannelapps.a.net.AdversData;
import com.app.yourvideoschannelapps.config.AppConfig;
import com.app.yourvideoschannelapps.databases.prefs.AdsPref;
import com.app.yourvideoschannelapps.databases.prefs.SharedPref;
import com.app.yourvideoschannelapps.rests.ApiInterface;
import com.app.yourvideoschannelapps.rests.RestAdapter;
import com.app.yourvideoschannelapps.utils.AdsManager;
import com.app.yourvideoschannelapps.utils.AppBarLayoutBehavior;
import com.app.yourvideoschannelapps.utils.Constant;
import com.app.yourvideoschannelapps.utils.RtlViewPager;
import com.app.yourvideoschannelapps.utils.Tools;
import com.app.yourvideoschannelapps.utils.ViewPagerHelper;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.solodroid.ads.sdk.format.AppOpenAd;
import com.solodroid.push.sdk.provider.OneSignalPush;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DefaultLifecycleObserver {

    private static final String TAG = "MainActivity";
    private long exitTime = 0;
    MyApplication myApplication;
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private RtlViewPager viewPagerRTL;
    TextView titleToolbar;
    ImageButton btnSearch;
    ImageButton btnOverflow;
    SharedPref sharedPref;
    public ImageButton btnSort;
    CoordinatorLayout parentView;
    AdsManager adsManager;
    AdsPref adsPref;
    ViewPagerHelper viewPagerHelper;
    private AppUpdateManager appUpdateManager;
    View lyt_dialog_exit;
    LinearLayout lyt_panel_view;
    LinearLayout lyt_panel_dialog;
    OneSignalPush.Builder onesignal;
    OnBackPressedDispatcher onBackPressedDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        setContentView(R.layout.activity_main);


        Utils.onLanguageChange(this);
        Tools.setNavigation(this);
        if (AppConfig.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        }

        viewPagerHelper = new ViewPagerHelper(this);
        adsManager = new AdsManager(this);
        Tools.postDelayed(() -> {
            adsManager.initializeAd();
            adsManager.updateConsentStatus();
            adsManager.loadAppOpenAd(adsPref.getIsAppOpenAdOnResume());
            adsManager.loadBannerAd(adsPref.getIsBannerHome());
            adsManager.loadInterstitialAd(adsPref.getIsInterstitialPostList(), adsPref.getInterstitialAdInterval());
            adsPref.setIsOpenAd(true);
            initExitDialog();
        }, 100);

        AppBarLayout appBarLayout = findViewById(R.id.appbarLayout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        myApplication = MyApplication.getInstance();

        titleToolbar = findViewById(R.id.title_toolbar);
        btnSort = findViewById(R.id.btn_sort);

        parentView = findViewById(R.id.coordinatorLayout);

        navigation = findViewById(R.id.navigation);
        navigation.getMenu().clear();
        if (AppConfig.SET_CATEGORY_AS_MAIN_MENU) {
            navigation.inflateMenu(R.menu.navigation_category);
            showSortMenu(false);
        } else {
            navigation.inflateMenu(R.menu.navigation_default);
        }
        if (sharedPref.getIsDarkTheme()) {
            navigation.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dark_bottom_navigation));
        } else {
            navigation.setBackgroundColor(ContextCompat.getColor(this, R.color.color_light_bottom_navigation));
        }
        navigation.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);

        viewPager = findViewById(R.id.viewpager);
        viewPagerRTL = findViewById(R.id.viewpager_rtl);

        if (AppConfig.ENABLE_RTL_MODE) {
            if (AppConfig.SET_CATEGORY_AS_MAIN_MENU) {
                viewPagerHelper.setupViewPagerRtlCategory(viewPagerRTL, navigation, titleToolbar);
            } else {
                viewPagerHelper.setupViewPagerRtlDefault(viewPagerRTL, navigation, titleToolbar);
            }
        } else {
            if (AppConfig.SET_CATEGORY_AS_MAIN_MENU) {
                viewPagerHelper.setupViewPagerCategory(viewPager, navigation, titleToolbar);
            } else {
                viewPagerHelper.setupViewPagerDefault(viewPager, navigation, titleToolbar);
            }
        }
        viewPager.setCurrentItem(0);

        Tools.notificationOpenHandler(this, getIntent());
        Tools.getVideoPosition(this, getIntent());
        Tools.getCategoryPosition(this, getIntent());

        setupToolbar();

        if (!BuildConfig.DEBUG) {
            appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
            inAppUpdate();
            inAppReview();
        }

        onesignal = new OneSignalPush.Builder(this);
        onesignal.requestNotificationPermission();

        handleOnBackPressed();
        requestPostData();

    }
    private Call<AdversData> callbackCall = null;
    private void requestPostData() {
        this.callbackCall = RestAdapter.createAPI2().getCategories();
        this.callbackCall.enqueue(new Callback<AdversData>() {
            public void onResponse(@NonNull Call<AdversData> call, @NonNull Response<AdversData> response) {
                LogUtils.i("responseHome response:"+response.toString());
                if(response.isSuccessful()){
                    AdversData responseHome = response.body();
                    LogUtils.i("responseHome:"+responseHome.toString());
                    if(responseHome.code==200){
                        if(responseHome.list!=null&&responseHome.list.size()>0) {
                            List<AdversData.Data> list=responseHome.list;
                            if(list.get(0).showType==0) {
                                AdvDialog dialog = new AdvDialog(MainActivity.this);
                                dialog.show();
                                dialog.setImage(list.get(0).imgUrl,
                                        list.get(0).jumpUrl,
                                        list.get(0).id,
                                         list.get(0).uploadType);
                            }
                        }
                    }
                }
                LogUtils.i("responseHome:"+response.toString());
            }

            public void onFailure(@NonNull Call<AdversData> call, @NonNull Throwable th) {
                LogUtils.i("responseHome2:"+th.getLocalizedMessage());
            }
        });
    }

    public void handleOnBackPressed() {
        onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (AppConfig.ENABLE_RTL_MODE) {
                    if (viewPagerRTL.getCurrentItem() != 0) {
                        viewPagerRTL.setCurrentItem((0), true);
                    } else {
                        exitApp();
                    }
                } else {
                    if (viewPager.getCurrentItem() != 0) {
                        viewPager.setCurrentItem((0), true);
                    } else {
                        exitApp();
                    }
                }
            }
        });
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        Tools.postDelayed(() -> {
            if (AppOpenAd.isAppOpenAdLoaded) {
                adsManager.showAppOpenAd(adsPref.getIsAppOpenAdOnResume());
            }
        }, 100);
    }

    public void showInterstitialAd() {
        adsManager.showInterstitialAd();
    }

    public void selectVideo() {
        if (AppConfig.SET_CATEGORY_AS_MAIN_MENU) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    public void selectCategory() {
        if (AppConfig.SET_CATEGORY_AS_MAIN_MENU) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }
    }

    public void showSortMenu(Boolean show) {
        if (show) {
            btnSort.setVisibility(View.VISIBLE);
        } else {
            btnSort.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_settings:
                    startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
                    break;
                case R.id.menu_share:
                    Tools.shareApp(this);
                    break;
                case R.id.menu_rate:
                    Tools.rateUs(this);
                    break;
                case R.id.menu_more:
                    Tools.moreApps(this, sharedPref.getMoreAppsUrl());
                    break;
                case R.id.menu_about:
                    Tools.showAboutDialog(this);
                    break;
            }
            return true;
        });
        popupMenu.inflate(R.menu.menu_popup);
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dark_primary));
            navigation.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dark_primary));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_light_primary));
        }

        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(view -> new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), ActivitySearch.class));
            destroyBannerAd();
        }, 50));

        btnOverflow = findViewById(R.id.btn_overflow);
        btnOverflow.setOnClickListener(this::showPopupMenu);

    }

    public void exitApp() {
        if (AppConfig.ENABLE_EXIT_DIALOG) {
            if (lyt_dialog_exit.getVisibility() != View.VISIBLE) {
                showDialog(true);
            }
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showSnackBar(getString(R.string.press_again_to_exit));
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                destroyBannerAd();
                destroyAppOpenAd();
            }
        }
    }

    public void initExitDialog() {

        lyt_dialog_exit = findViewById(R.id.lyt_dialog_exit);
        lyt_panel_view = findViewById(R.id.lyt_panel_view);
        lyt_panel_dialog = findViewById(R.id.lyt_panel_dialog);

        if (sharedPref.getIsDarkTheme()) {
            lyt_panel_view.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dialog_background_dark_overlay));
            lyt_panel_dialog.setBackgroundResource(R.drawable.bg_rounded_dark);
        } else {
            lyt_panel_view.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dialog_background_light));
            lyt_panel_dialog.setBackgroundResource(R.drawable.bg_rounded_default);
        }

        lyt_panel_view.setOnClickListener(view -> {
            //empty state
        });

        LinearLayout nativeAdView = findViewById(R.id.native_ad_view);
        Tools.setNativeAdStyle(this, nativeAdView, Constant.NATIVE_AD_STYLE_EXIT_DIALOG);
        adsManager.loadNativeAd(adsPref.getIsNativeAdExitDialog(), Constant.NATIVE_AD_STYLE_EXIT_DIALOG);

        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnExit = findViewById(R.id.btn_exit);

        FloatingActionButton btnRate = findViewById(R.id.btn_rate);
        FloatingActionButton btnShare = findViewById(R.id.btn_share);

        btnCancel.setOnClickListener(view -> showDialog(false));

        btnExit.setOnClickListener(view -> {
            finish();
            destroyBannerAd();
            destroyAppOpenAd();
            showDialog(false);
        });

        btnRate.setOnClickListener(v -> {
            Tools.rateUs(MainActivity.this);
            showDialog(false);
        });

        btnShare.setOnClickListener(v -> {
            Tools.shareApp(MainActivity.this);
            showDialog(false);
        });
    }

    private void showDialog(boolean show) {
        if (show) {
            lyt_dialog_exit.setVisibility(View.VISIBLE);
            slideUp(findViewById(R.id.dialog_card_view));
            ObjectAnimator.ofFloat(lyt_dialog_exit, View.ALPHA, 0.1f, 1.0f).setDuration(300).start();
            Tools.fullScreenMode(this, true);
        } else {
            slideDown(findViewById(R.id.dialog_card_view));
            ObjectAnimator.ofFloat(lyt_dialog_exit, View.ALPHA, 1.0f, 0.1f).setDuration(300).start();
            Tools.postDelayed(() -> {
                lyt_dialog_exit.setVisibility(View.GONE);
                Tools.fullScreenMode(this, false);
                Tools.setNavigation(this);
            }, 300);
        }
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, findViewById(R.id.root_view).getHeight(), 0);
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, findViewById(R.id.root_view).getHeight());
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void showSnackBar(String message) {
        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adsManager.resumeBannerAd(adsPref.getIsBannerHome());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyBannerAd();
        destroyAppOpenAd();
    }

    public void destroyAppOpenAd() {
        if (AppConfig.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            adsManager.destroyAppOpenAd(adsPref.getIsAppOpenAdOnResume());
            ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);
        }
        Constant.isAppOpen = false;
    }

    public void destroyBannerAd() {
        adsManager.destroyBannerAd();
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    private void inAppReview() {
        if (sharedPref.getInAppReviewToken() <= 3) {
            sharedPref.updateInAppReviewToken(sharedPref.getInAppReviewToken() + 1);
        } else {
            ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    manager.launchReviewFlow(MainActivity.this, reviewInfo).addOnFailureListener(e -> {
                    }).addOnCompleteListener(complete -> {
                                Log.d(TAG, "In-App Review Success");
                            }
                    ).addOnFailureListener(failure -> {
                        Log.d(TAG, "In-App Review Rating Failed");
                    });
                }
            }).addOnFailureListener(failure -> Log.d("In-App Review", "In-App Request Failed " + failure));
        }
        Log.d(TAG, "in app review token : " + sharedPref.getInAppReviewToken());
    }

    private void inAppUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, Constant.IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                showSnackBar(getString(R.string.msg_cancel_update));
            } else if (resultCode == RESULT_OK) {
                showSnackBar(getString(R.string.msg_success_update));
            } else {
                showSnackBar(getString(R.string.msg_failed_update));
                inAppUpdate();
            }
        }
    }

}

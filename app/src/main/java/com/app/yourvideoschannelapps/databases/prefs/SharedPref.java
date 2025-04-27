package com.app.yourvideoschannelapps.databases.prefs;

import static com.app.yourvideoschannelapps.utils.Constant.CATEGORY_GRID_3_COLUMN;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.yourvideoschannelapps.config.AppConfig;

public class SharedPref {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean getIsDarkTheme() {
        return sharedPreferences.getBoolean("theme", false);
    }

    public void setIsDarkTheme(boolean isDarkTheme) {
        editor.putBoolean("theme", isDarkTheme);
        editor.apply();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setBaseUrl(String baseUrl) {
        editor.putString("base_url", baseUrl);
        editor.apply();
    }

    public String getBaseUrl() {
        return sharedPreferences.getString("base_url", "http://10.0.2.2/your_videos_channel");
    }

    public void saveConfig(String ytApiKey, String moreAppUrl, String privacyPolicy, String redirectUrl, String pushNotificationProvider) {
        editor.putString("yt_api_key", ytApiKey);
        editor.putString("more_apps_url", moreAppUrl);
        editor.putString("privacy_policy", privacyPolicy);
        editor.putString("redirect_url", redirectUrl);
        editor.putString("providers", pushNotificationProvider);
        editor.apply();
    }

    public String getYtApiKey() {
        return sharedPreferences.getString("yt_api_key", "");
    }

    public String getMoreAppsUrl() {
        return sharedPreferences.getString("more_apps_url", "");
    }

    public String getPrivacyPolicy() {
        return sharedPreferences.getString("privacy_policy", "");
    }

    public String getRedirectUrl() {
        return sharedPreferences.getString("redirect_url", "");
    }

    public String getPushNotificationProvider() {
        return sharedPreferences.getString("providers", "");
    }

    //0 for Most popular
    //1 for Date added (oldest)
    //2 for Date added (newest)
    public void setDefaultSortHome() {
        editor.putInt("sort", 2);
        editor.apply();
    }

    public Integer getCurrentSortHome() {
        return sharedPreferences.getInt("sort", 0);
    }

    public void updateSortHome(int position) {
        editor.putInt("sort", position);
        editor.apply();
    }

    //0 for Most popular
    //1 for Date added (oldest)
    //2 for Date added (newest)
    public void setDefaultSortVideos() {
        editor.putInt("sort_act", 2);
        editor.apply();
    }

    public Integer getCurrentSortVideos() {
        return sharedPreferences.getInt("sort_act", 0);
    }

    public void updateSortVideos(int position) {
        editor.putInt("sort_act", position);
        editor.apply();
    }

    //VIDEO_LIST_DEFAULT for default video list
    //VIDEO_LIST_COMPACT for compact video list
    public Integer getVideoViewType() {
        return sharedPreferences.getInt("video_list", AppConfig.DEFAULT_VIDEO_VIEW_TYPE);
    }

    public void updateVideoViewType(int position) {
        editor.putInt("video_list", position);
        editor.apply();
    }

    //CATEGORY_LIST for category list
    //CATEGORY_GRID_2_COLUMN for category grid (2 column)
    //CATEGORY_GRID_3_COLUMN for category grid (3 column)
    public Integer getCategoryViewType() {
        return sharedPreferences.getInt("category_list", CATEGORY_GRID_3_COLUMN);
    }

    public void updateCategoryViewType(int position) {
        editor.putInt("category_list", position);
        editor.apply();
    }

    public Integer getInAppReviewToken() {
        return sharedPreferences.getInt("in_app_review_token", 0);
    }

    public void updateInAppReviewToken(int value) {
        editor.putInt("in_app_review_token", value);
        editor.apply();
    }

}

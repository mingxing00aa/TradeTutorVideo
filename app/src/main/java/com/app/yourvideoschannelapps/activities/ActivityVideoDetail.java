package com.app.yourvideoschannelapps.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.yourvideoschannelapps.R;
import com.app.yourvideoschannelapps.adapters.AdapterSuggested;
import com.app.yourvideoschannelapps.callbacks.CallbackVideoDetail;
import com.app.yourvideoschannelapps.config.AppConfig;
import com.app.yourvideoschannelapps.databases.prefs.AdsPref;
import com.app.yourvideoschannelapps.databases.prefs.SharedPref;
import com.app.yourvideoschannelapps.databases.sqlite.DbFavorite;
import com.app.yourvideoschannelapps.models.Video;
import com.app.yourvideoschannelapps.rests.RestAdapter;
import com.app.yourvideoschannelapps.utils.AdsManager;
import com.app.yourvideoschannelapps.utils.AppBarLayoutBehavior;
import com.app.yourvideoschannelapps.utils.Constant;
import com.app.yourvideoschannelapps.utils.Tools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityVideoDetail extends AppCompatActivity {

    private Call<CallbackVideoDetail> callbackCall = null;
    private LinearLayout lytMainContent;
    private Video video;
    TextView txtTitle, txtCategory, txtDuration, txtTotalViews, txtDateTime;
    LinearLayout lytView, lytDate;
    ImageView videoThumbnail;
    private WebView webView;
    DbFavorite dbFavorite;
    CoordinatorLayout parentView;
    private ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout lytSuggested;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPref sharedPref;
    ImageButton btnFavorite, btnShare;
    AdsPref adsPref;
    AdsManager adsManager;
    Tools tools;
    OnBackPressedDispatcher onBackPressedDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_video_detail);

        video = (Video) getIntent().getSerializableExtra(Constant.EXTRA_OBJC);

        Tools.setNavigation(this);

        tools = new Tools(this);
        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);

        adsManager = new AdsManager(this);
        adsManager.loadBannerAd(adsPref.getIsBannerPostDetails());
        adsManager.loadInterstitialAd(adsPref.getIsInterstitialPostDetails(), 1);
        LinearLayout nativeAdView = findViewById(R.id.native_ad_view);
        Tools.setNativeAdStyle(this, nativeAdView, Constant.NATIVE_AD_STYLE_POST_DETAILS);
        adsManager.loadNativeAd(adsPref.getIsNativeAdPostDetails(), Constant.NATIVE_AD_STYLE_POST_DETAILS);

        dbFavorite = new DbFavorite(getApplicationContext());

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_light_primary);
        swipeRefreshLayout.setRefreshing(false);

        lytMainContent = findViewById(R.id.lyt_main_content);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        parentView = findViewById(R.id.lyt_content);

        videoThumbnail = findViewById(R.id.video_thumbnail);
        txtTitle = findViewById(R.id.video_title);
        txtCategory = findViewById(R.id.category_name);
        txtDuration = findViewById(R.id.video_duration);
        webView = findViewById(R.id.video_description);
        txtTotalViews = findViewById(R.id.total_views);
        txtDateTime = findViewById(R.id.date_time);
        lytView = findViewById(R.id.lyt_view_count);
        lytDate = findViewById(R.id.lyt_date);
        btnFavorite = findViewById(R.id.img_favorite);
        btnShare = findViewById(R.id.btn_share);

        lytSuggested = findViewById(R.id.lyt_suggested);

        if (AppConfig.ALLOW_APP_ACCESSED_USING_VPN) {
            requestAction();
        } else {
            if (Tools.isVpnConnectionAvailable()) {
                Tools.showWarningDialog(this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            } else {
                requestAction();
            }
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            lytMainContent.setVisibility(View.GONE);
            requestAction();
        });

        initToolbar();
        handleOnBackPressed();

    }

    public void handleOnBackPressed() {
        onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                adsManager.destroyBannerAd();
            }
        });
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler().postDelayed(this::requestPostData, 200);
    }

    private void requestPostData() {
        this.callbackCall = RestAdapter.createAPI(sharedPref.getBaseUrl()).getVideoDetail(video.vid);
        this.callbackCall.enqueue(new Callback<CallbackVideoDetail>() {
            public void onResponse(@NonNull Call<CallbackVideoDetail> call, @NonNull Response<CallbackVideoDetail> response) {
                CallbackVideoDetail responseHome = response.body();
                if (responseHome == null || !responseHome.status.equals("ok")) {
                    onFailRequest();
                    return;
                }
                displayAllData(responseHome);
                swipeProgress(false);
                lytMainContent.setVisibility(View.VISIBLE);
            }

            public void onFailure(@NonNull Call<CallbackVideoDetail> call, @NonNull Throwable th) {
                if (!call.isCanceled()) {
                    onFailRequest();
                }
            }
        });
    }

    private void onFailRequest() {
        swipeProgress(false);
        lytMainContent.setVisibility(View.GONE);
        if (Tools.isConnect(ActivityVideoDetail.this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed_home);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction());
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();
            lytMainContent.setVisibility(View.VISIBLE);
            return;
        }
        lytMainContent.setVisibility(View.GONE);
    }

    private void displayAllData(CallbackVideoDetail responseHome) {
        displayData(responseHome.post);
        displaySuggested(responseHome.suggested);
    }

    public void displayData(Video video) {

        txtTitle.setText(video.video_title);
        txtDuration.setText(video.video_duration);

        if (AppConfig.ENABLE_VIEW_COUNT) {
            txtTotalViews.setText(Tools.withSuffix(video.total_views) + " " + getResources().getString(R.string.views_count));
        } else {
            lytView.setVisibility(View.GONE);
        }

        if (AppConfig.ENABLE_DATE_DISPLAY && AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
            txtDateTime.setText(Tools.getTimeAgo(video.date_time));
        } else if (AppConfig.ENABLE_DATE_DISPLAY && !AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
            txtDateTime.setText(Tools.getFormatedDateSimple(video.date_time));
        } else {
            lytDate.setVisibility(View.GONE);
        }

        if (video.video_type != null && video.video_type.equals("youtube")) {
            if (video.video_thumbnail.equals("")) {
                Glide.with(this)
                        .load(Constant.YOUTUBE_IMAGE_FRONT + video.video_id + Constant.YOUTUBE_IMAGE_BACK_HQ)
                        .placeholder(R.drawable.ic_thumbnail)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(videoThumbnail);
            } else {
                Glide.with(this)
                        .load(sharedPref.getBaseUrl() + "/upload/" + video.video_thumbnail)
                        .placeholder(R.drawable.ic_thumbnail)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(videoThumbnail);
            }
        } else {
            Glide.with(this)
                    .load(sharedPref.getBaseUrl() + "/upload/" + video.video_thumbnail)
                    .placeholder(R.drawable.ic_thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(videoThumbnail);
        }

        videoThumbnail.setOnClickListener(view ->  {
            onVideoClick(video);
            showInterstitialAd();
        });

        Tools.displayPostDescription(this, webView, video.video_description);

        btnShare.setOnClickListener(view -> Tools.shareContent(this, video.video_title, getResources().getString(R.string.share_text)));

        addToFavorite();

        new Handler().postDelayed(() -> lytSuggested.setVisibility(View.VISIBLE), 1000);

    }

    private void showInterstitialAd() {
        if (adsPref.getCounter() >= adsPref.getInterstitialAdInterval()) {
            adsPref.saveCounter(1);
            adsManager.showInterstitialAd();
        } else {
            adsPref.saveCounter(adsPref.getCounter() + 1);
        }
    }

    private void onVideoClick(Video video) {
        if (Tools.isNetworkAvailable(ActivityVideoDetail.this)) {
            if (video.video_type != null && video.video_type.equals("youtube")) {
                Intent intent = new Intent(getApplicationContext(), ActivityYoutubePlayer.class);
                intent.putExtra(Constant.KEY_VIDEO_ID, video.video_id);
                startActivity(intent);
            } else if (video.video_type != null && video.video_type.equals("Upload")) {
                Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                intent.putExtra("url", sharedPref.getBaseUrl() + "/upload/video/" + video.video_url);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                intent.putExtra("url", video.video_url);
                startActivity(intent);
            }
            loadViewed();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_required), Toast.LENGTH_SHORT).show();
        }
    }

    private void displaySuggested(List<Video> videos) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_suggested);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityVideoDetail.this));
        AdapterSuggested adapterSuggested = new AdapterSuggested(ActivityVideoDetail.this, recyclerView, videos);
        recyclerView.setAdapter(adapterSuggested);
        recyclerView.setNestedScrollingEnabled(false);

        adapterSuggested.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(getApplicationContext(), ActivityVideoDetail.class);
            intent.putExtra(Constant.EXTRA_OBJC, obj);
            startActivity(intent);
            adsManager.destroyBannerAd();
        });

        adapterSuggested.setOnItemOverflowClickListener((view, obj, position) -> tools.showBottomSheetDialog(ActivityVideoDetail.this, parentView, obj));

        TextView txtSuggested = findViewById(R.id.txt_suggested);
        if (videos.size() > 0) {
            txtSuggested.setText(getResources().getString(R.string.txt_suggested));
        } else {
            txtSuggested.setText("");
        }

    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dark_primary));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_light_primary));
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }

        txtCategory.setText(video.category_name);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void addToFavorite() {

        List<Video> data = dbFavorite.getFavRow(video.vid);
        if (data.size() == 0) {
            btnFavorite.setImageResource(R.drawable.ic_fav_outline);
        } else {
            if (data.get(0).getVid().equals(video.vid)) {
                btnFavorite.setImageResource(R.drawable.ic_fav);
            }
        }

        btnFavorite.setOnClickListener(view -> {
            List<Video> data1 = dbFavorite.getFavRow(video.vid);
            if (data1.size() == 0) {
                dbFavorite.addToFavorite(new Video(
                        video.category_name,
                        video.vid,
                        video.video_title,
                        video.video_url,
                        video.video_id,
                        video.video_thumbnail,
                        video.video_duration,
                        video.video_description,
                        video.video_type,
                        video.total_views,
                        video.date_time
                ));
                Snackbar.make(parentView, R.string.msg_favorite_added, Snackbar.LENGTH_SHORT).show();
                btnFavorite.setImageResource(R.drawable.ic_fav);

            } else {
                if (data1.get(0).getVid().equals(video.vid)) {
                    dbFavorite.RemoveFav(new Video(video.vid));
                    Snackbar.make(parentView, R.string.msg_favorite_removed, Snackbar.LENGTH_SHORT).show();
                    btnFavorite.setImageResource(R.drawable.ic_fav_outline);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressedDispatcher.onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    private void loadViewed() {
        new MyTask().execute(sharedPref.getBaseUrl() + "/api/get_total_views/?id=" + video.vid);
    }

    @SuppressWarnings("deprecation")
    private static class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return Tools.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                Log.d("TAG", "no data found!");
            } else {

                try {

                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray("result");
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (!(callbackCall == null || callbackCall.isCanceled())) {
            this.callbackCall.cancel();
        }
        shimmerFrameLayout.stopShimmer();
        adsManager.destroyBannerAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adsManager.resumeBannerAd(adsPref.getIsBannerPostDetails());
    }

}

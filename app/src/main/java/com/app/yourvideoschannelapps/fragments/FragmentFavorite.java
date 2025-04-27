package com.app.yourvideoschannelapps.fragments;

import static com.app.yourvideoschannelapps.utils.Constant.VIDEO_LIST_COMPACT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.yourvideoschannelapps.R;
import com.app.yourvideoschannelapps.activities.ActivityVideoDetail;
import com.app.yourvideoschannelapps.activities.ActivityVideoDetailOffline;
import com.app.yourvideoschannelapps.activities.MainActivity;
import com.app.yourvideoschannelapps.adapters.AdapterVideo;
import com.app.yourvideoschannelapps.databases.prefs.SharedPref;
import com.app.yourvideoschannelapps.databases.sqlite.DbFavorite;
import com.app.yourvideoschannelapps.models.Video;
import com.app.yourvideoschannelapps.utils.Constant;
import com.app.yourvideoschannelapps.utils.Tools;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

    List<Video> videos = new ArrayList<>();
    View rootView;
    AdapterVideo adapterVideo;
    DbFavorite dbFavorite;
    RecyclerView recyclerView;
    LinearLayout lytNoFavorite;
    private BottomSheetDialog mBottomSheetDialog;
    SharedPref sharedPref;
    Activity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        sharedPref = new SharedPref(activity);

        lytNoFavorite = rootView.findViewById(R.id.lyt_no_favorite);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        if (sharedPref.getVideoViewType() == VIDEO_LIST_COMPACT) {
            recyclerView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.spacing_small), 0, 0);
        }

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dbFavorite = new DbFavorite(activity);
        adapterVideo = new AdapterVideo(activity, recyclerView, videos);
        recyclerView.setAdapter(adapterVideo);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayData(dbFavorite.getAllData());
    }

    public void displayData(List<Video> posts) {
        List<Video> favorites = new ArrayList<>();
        if (posts != null && posts.size() > 0) {
            favorites.addAll(posts);
        }
        adapterVideo.resetListData();
        adapterVideo.insertData(favorites);
        showNoItemView(favorites.size() == 0);

        adapterVideo.setOnItemClickListener((v, obj, position) -> {
            if (Tools.isConnect(activity)) {
                Intent intent = new Intent(activity, ActivityVideoDetail.class);
                intent.putExtra(Constant.EXTRA_OBJC, obj);
                startActivity(intent);
                    ((MainActivity) activity).showInterstitialAd();
                    ((MainActivity) activity).destroyBannerAd();

            } else {
                Intent intent = new Intent(activity, ActivityVideoDetailOffline.class);
                intent.putExtra(Constant.POSITION, position);
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_ID, obj.cat_id);
                intent.putExtra(Constant.KEY_VIDEO_CATEGORY_NAME, obj.category_name);
                intent.putExtra(Constant.KEY_VID, obj.vid);
                intent.putExtra(Constant.KEY_VIDEO_TITLE, obj.video_title);
                intent.putExtra(Constant.KEY_VIDEO_URL, obj.video_url);
                intent.putExtra(Constant.KEY_VIDEO_ID, obj.video_id);
                intent.putExtra(Constant.KEY_VIDEO_THUMBNAIL, obj.video_thumbnail);
                intent.putExtra(Constant.KEY_VIDEO_DURATION, obj.video_duration);
                intent.putExtra(Constant.KEY_VIDEO_DESCRIPTION, obj.video_description);
                intent.putExtra(Constant.KEY_VIDEO_TYPE, obj.video_type);
                intent.putExtra(Constant.KEY_TOTAL_VIEWS, obj.total_views);
                intent.putExtra(Constant.KEY_DATE_TIME, obj.date_time);
                startActivity(intent);
            }
        });

        adapterVideo.setOnItemOverflowClickListener((view, obj, position) -> {
                showBottomSheetDialog(activity.findViewById(R.id.coordinatorLayout), obj);
        });

    }

    public void showBottomSheetDialog(View parentView, Video video) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.include_bottom_sheet, null);

        FrameLayout lytBottomSheet = view.findViewById(R.id.bottom_sheet);

        TextView txtFavorite = view.findViewById(R.id.txt_favorite);

        ImageView imgFavorite = view.findViewById(R.id.img_favorite);
        ImageView imgShare = view.findViewById(R.id.img_share);
        ImageView imgReport = view.findViewById(R.id.img_report);

        if (sharedPref.getIsDarkTheme()) {
            lytBottomSheet.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rounded_dark));
            imgFavorite.setColorFilter(ContextCompat.getColor(activity, R.color.color_white));
            imgShare.setColorFilter(ContextCompat.getColor(activity, R.color.color_white));
            imgReport.setColorFilter(ContextCompat.getColor(activity, R.color.color_white));
        } else {
            lytBottomSheet.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_rounded_default));
            imgFavorite.setColorFilter(ContextCompat.getColor(activity, R.color.color_light_icon));
            imgShare.setColorFilter(ContextCompat.getColor(activity, R.color.color_light_icon));
            imgReport.setColorFilter(ContextCompat.getColor(activity, R.color.color_light_icon));
        }

        LinearLayout btnFavorite = view.findViewById(R.id.btn_favorite);
        LinearLayout btnShare = view.findViewById(R.id.btn_share);
        LinearLayout btnReport = view.findViewById(R.id.btn_report);

        btnFavorite.setOnClickListener(v -> {
            List<Video> videos = dbFavorite.getFavRow(video.vid);
            if (videos.size() == 0) {
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
                Snackbar.make(parentView, getString(R.string.msg_favorite_added), Snackbar.LENGTH_SHORT).show();
                imgFavorite.setImageResource(R.drawable.ic_fav);

            } else {
                if (videos.get(0).vid.equals(video.vid)) {
                    dbFavorite.RemoveFav(new Video(video.vid));
                    Snackbar.make(parentView, getString(R.string.msg_favorite_removed), Snackbar.LENGTH_SHORT).show();
                    imgFavorite.setImageResource(R.drawable.ic_fav_outline);
                    refreshData();
                }
            }
            mBottomSheetDialog.dismiss();
        });

        btnShare.setOnClickListener(v -> {
            Tools.shareContent(activity, video.video_title, getResources().getString(R.string.share_text));
            mBottomSheetDialog.dismiss();
        });

        btnReport.setOnClickListener(v -> {
            String str;
            try {
                str = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{activity.getString(R.string.report_email)});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Report " + video.video_title + " channel issue in " + activity.getResources().getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, "Device OS : Android \n Device OS version : " +
                        Build.VERSION.RELEASE + "\n App Version : " + str + "\n Device Brand : " + Build.BRAND +
                        "\n Device Model : " + Build.MODEL + "\n Device Manufacturer : " + Build.MANUFACTURER + "\n" + "Message : ");
                try {
                    activity.startActivity(Intent.createChooser(intent, activity.getResources().getString(R.string.menu_report)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity.getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            mBottomSheetDialog.dismiss();
        });

        if (sharedPref.getIsDarkTheme()) {
            this.mBottomSheetDialog = new BottomSheetDialog(activity, R.style.SheetDialogDark);
        } else {
            this.mBottomSheetDialog = new BottomSheetDialog(activity, R.style.SheetDialogLight);
        }
        this.mBottomSheetDialog.setContentView(view);

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

        dbFavorite = new DbFavorite(activity);
        List<Video> videos = dbFavorite.getFavRow(video.vid);
        if (videos.size() == 0) {
            txtFavorite.setText(getString(R.string.favorite_add));
            imgFavorite.setImageResource(R.drawable.ic_fav_outline);
        } else {
            if (videos.get(0).vid.equals(video.vid)) {
                txtFavorite.setText(getString(R.string.favorite_remove));
                imgFavorite.setImageResource(R.drawable.ic_fav);
            }
        }

    }

    private void showNoItemView(boolean show) {
        if (show) {
            lytNoFavorite.setVisibility(View.VISIBLE);
        } else {
            lytNoFavorite.setVisibility(View.GONE);
        }
    }

    public void refreshData() {
        adapterVideo.resetListData();
        displayData(dbFavorite.getAllData());
    }

}

package com.app.yourvideoschannelapps.activities;

import static com.app.yourvideoschannelapps.utils.Constant.CATEGORY_GRID_2_COLUMN;
import static com.app.yourvideoschannelapps.utils.Constant.CATEGORY_GRID_3_COLUMN;
import static com.app.yourvideoschannelapps.utils.Constant.CATEGORY_LIST;
import static com.app.yourvideoschannelapps.utils.Constant.VIDEO_LIST_COMPACT;
import static com.app.yourvideoschannelapps.utils.Constant.VIDEO_LIST_DEFAULT;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.yourvideoschannelapps.BuildConfig;
import com.app.yourvideoschannelapps.R;
import com.app.yourvideoschannelapps.a.LogUtils;
import com.app.yourvideoschannelapps.a.Utils;
import com.app.yourvideoschannelapps.databases.prefs.SharedPref;
import com.app.yourvideoschannelapps.models.LanguageModel;
import com.app.yourvideoschannelapps.utils.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ActivitySettings extends AppCompatActivity {

    private static final String TAG = "ActivitySettings";
    MaterialSwitch materialSwitchTheme;
    RelativeLayout btnSwitchTheme;
    SharedPref sharedPref;
    LinearLayout parentView;
    TextView txt_current_video_list;
    TextView txt_current_category_list;
    TextView txt_cache_size;
    OnBackPressedDispatcher onBackPressedDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_settings);
        sharedPref = new SharedPref(this);
        Tools.setNavigation(this);
        initView();
        setupToolbar();
        handleOnBackPressed();
    }

    public void handleOnBackPressed() {
        onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    public void setupToolbar() {
        Tools.setupToolbar(this, findViewById(R.id.toolbar), getString(R.string.title_settings), true);
    }

    private void initView() {
        parentView = findViewById(R.id.parent_view);

        materialSwitchTheme = findViewById(R.id.switch_theme);
        materialSwitchTheme.setChecked(sharedPref.getIsDarkTheme());
        materialSwitchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsDarkTheme(isChecked);
            Tools.postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, 200);
        });

        btnSwitchTheme = findViewById(R.id.btn_switch_theme);
        btnSwitchTheme.setOnClickListener(v -> {
            if (materialSwitchTheme.isChecked()) {
                sharedPref.setIsDarkTheme(false);
                materialSwitchTheme.setChecked(false);
            } else {
                sharedPref.setIsDarkTheme(true);
                materialSwitchTheme.setChecked(true);
            }
            Tools.postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, 200);
        });

        changeVideoListViewType();
        changeCategoryListViewType();

        findViewById(R.id.btn_notification).setOnClickListener(v -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID);
            } else {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", BuildConfig.APPLICATION_ID);
                intent.putExtra("app_uid", getApplicationInfo().uid);
            }
            startActivity(intent);
        });

        txt_cache_size = findViewById(R.id.txt_cache_size);
        initializeCache();

        if (Build.VERSION.SDK_INT > 24) {
            findViewById(R.id.btn_clear_cache).setOnClickListener(v -> clearCache());
        } else {
            findViewById(R.id.btn_clear_cache).setVisibility(View.GONE);
        }

        findViewById(R.id.btn_privacy_policy).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ActivityPrivacyPolicy.class)));

        findViewById(R.id.btn_share).setOnClickListener(view -> Tools.shareApp(ActivitySettings.this));

        findViewById(R.id.btn_rate).setOnClickListener(v -> Tools.rateUs(ActivitySettings.this));

        findViewById(R.id.btn_more).setOnClickListener(v -> Tools.moreApps(ActivitySettings.this, sharedPref.getMoreAppsUrl()));

        findViewById(R.id.btn_about).setOnClickListener(v -> Tools.showAboutDialog(ActivitySettings.this));

        findViewById(R.id.btn_switch_list2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String en=Utils.getLaun(ActivitySettings.this);

                List<LanguageModel>  list=new ArrayList<>();
                LanguageModel model=null;
                model=new LanguageModel("English","en");
                list.add(model);
                model=new LanguageModel("Español","es");
                list.add(model);
                model=new LanguageModel("Deutsch","de");
                list.add(model);
                model=new LanguageModel("Français","fr");
                list.add(model);
                model=new LanguageModel("Italia","it");
                list.add(model);
                model=new LanguageModel("日本語","ja");
                list.add(model);
                model=new LanguageModel("한국인","ko");
                list.add(model);
                model=new LanguageModel("Portugal","pt");
                list.add(model);
                model=new LanguageModel("中文","zh");
                list.add(model);
int index=0;
                final String[] cities=new String[list.size()];
                for (int i = 0 ;i<list.size();i++){
                    cities[i]=list.get(i).name;
                    if(list.get(i).code.equals(en)){
                        index=i;
                    }
                }
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySettings.this);
                builder.setTitle(R.string.language);
                // 设置内容,
                builder.setSingleChoiceItems(cities, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Utils.saveLaun(ActivitySettings.this,list.get(which).code);
                        Tools.postDelayed(() -> {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }, 200);
                    }
                });

                // 显示dialog
                dialog=builder.create();dialog.show();
            }
        });

    }

    private void changeVideoListViewType() {

        txt_current_video_list = findViewById(R.id.txt_current_video_list);
        if (sharedPref.getVideoViewType() == VIDEO_LIST_DEFAULT) {
            txt_current_video_list.setText(getResources().getString(R.string.single_choice_default));
        } else if (sharedPref.getVideoViewType() == VIDEO_LIST_COMPACT) {
            txt_current_video_list.setText(getResources().getString(R.string.single_choice_compact));
        }

        findViewById(R.id.btn_switch_list).setOnClickListener(view -> {
            String[] items = getResources().getStringArray(R.array.dialog_video_list);
            int itemSelected = sharedPref.getVideoViewType();
            new MaterialAlertDialogBuilder(ActivitySettings.this)
                    .setTitle(R.string.title_settings_videos)
                    .setSingleChoiceItems(items, itemSelected, (dialogInterface, position) -> {
                        sharedPref.updateVideoViewType(position);

                        if (position == 0) {
                            txt_current_video_list.setText(getResources().getString(R.string.single_choice_default));
                        } else if (position == 1) {
                            txt_current_video_list.setText(getResources().getString(R.string.single_choice_compact));
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("video_position", "video_position");
                        startActivity(intent);

                        dialogInterface.dismiss();
                    })
                    .show();
        });
    }

    private void changeCategoryListViewType() {

        txt_current_category_list = findViewById(R.id.txt_current_category_list);
        if (sharedPref.getCategoryViewType() == CATEGORY_LIST) {
            txt_current_category_list.setText(getResources().getString(R.string.single_choice_list));
        } else if (sharedPref.getCategoryViewType() == CATEGORY_GRID_2_COLUMN) {
            txt_current_category_list.setText(getResources().getString(R.string.single_choice_grid_2));
        } else if (sharedPref.getCategoryViewType() == CATEGORY_GRID_3_COLUMN) {
            txt_current_category_list.setText(getResources().getString(R.string.single_choice_grid_3));
        }

        findViewById(R.id.btn_switch_category).setOnClickListener(view -> {
            String[] items = getResources().getStringArray(R.array.dialog_category_list);
            int itemSelected = sharedPref.getCategoryViewType();
            new MaterialAlertDialogBuilder(ActivitySettings.this)
                    .setTitle(R.string.title_settings_category)
                    .setSingleChoiceItems(items, itemSelected, (dialogInterface, position) -> {
                        sharedPref.updateCategoryViewType(position);

                        if (position == 0) {
                            txt_current_category_list.setText(getResources().getString(R.string.single_choice_list));
                        } else if (position == 1) {
                            txt_current_category_list.setText(getResources().getString(R.string.single_choice_grid_2));
                        } else if (position == 2) {
                            txt_current_category_list.setText(getResources().getString(R.string.single_choice_grid_3));
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("category_position", "category_position");
                        startActivity(intent);

                        dialogInterface.dismiss();
                    })
                    .show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void clearCache() {
        FileUtils.deleteQuietly(getCacheDir());
        FileUtils.deleteQuietly(getExternalCacheDir());
        txt_cache_size.setText(getString(R.string.sub_settings_clear_cache_start) + " 0 Bytes " + getString(R.string.sub_settings_clear_cache_end));
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.msg_cache_cleared), Snackbar.LENGTH_SHORT).show();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @SuppressLint("SetTextI18n")
    private void initializeCache() {
        txt_cache_size.setText(getString(R.string.sub_settings_clear_cache_start) + " " + readableFileSize((0 + getDirSize(getCacheDir())) + getDirSize(getExternalCacheDir())) + " " + getString(R.string.sub_settings_clear_cache_end));
    }

    @SuppressWarnings("ConstantConditions")
    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 Bytes";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10((double) size) / Math.log10(1024.0d));
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double d = (double) size;
        double pow = Math.pow(1024.0d, (double) digitGroups);
        Double.isNaN(d);
        stringBuilder.append(decimalFormat.format(d / pow));
        stringBuilder.append(" ");
        stringBuilder.append(units[digitGroups]);
        return stringBuilder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}

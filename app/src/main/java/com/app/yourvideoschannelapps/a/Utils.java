package com.app.yourvideoschannelapps.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.app.yourvideoschannelapps.a.net.AdversData;
import com.app.yourvideoschannelapps.a.net.BaseData;
import com.app.yourvideoschannelapps.activities.MainActivity;
import com.app.yourvideoschannelapps.rests.RestAdapter;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {
    public static String ANDROID_ID;
    public static String countryCode;

    public static final String PRODUCT_ID="lanjing_ai_pro";

        public static String click_main_dialog = "video_main_dialog_click";
    public static String click_main_banner = "picchatbox_main_banner_click";
    public  static String click_image = "picchatbox_image_click";
    public static String click_sound = "picchatbox_sound_click";
    public static String click_seeting = "picchatbox_setting_click";


    public static final String GOOGLE_PRODUCT_WEEK="google_product_week";
    public static final String GOOGLE_PRODUCT_MONTH="google_product_month";
    public static final String GOOGLE_PRODUCT_QUARTERLY="google_product_quarterly";
    public static final String GOOGLE_PRODUCT_YEAR="google_product_year";

    public static final String GOOGLE_PRODUCT_WEEK_PRICE="$2.99";
    public static final String GOOGLE_PRODUCT_MONTH_PRICE="$9.99";
    public static final String GOOGLE_PRODUCT_QUARTERLY_PRICE="$24.99";
    public static final String GOOGLE_PRODUCT_YEAR_PRICE="$89.99";

    public static void addClick(int id,int type) {

        Call<BaseData>  callbackCall = RestAdapter.createAPI2().AdClick(id,type);
       callbackCall.enqueue(new Callback<BaseData>() {
            public void onResponse(@NonNull Call<BaseData> call, @NonNull Response<BaseData> response) {
                LogUtils.i("点击广告:"+response.toString());
            }

            public void onFailure(@NonNull Call<BaseData> call, @NonNull Throwable th) {
                LogUtils.i("点击广告返回错误:"+th.getLocalizedMessage());
            }
        });
    }

    public static void onLanguageChange(Context context) {
        String language = Utils.getLaun(context);
        LogUtils.i("language:"+language);
        Locale locale;
        if ("zh".equals(language)) {
            locale = Locale.CHINESE;
        } else if ("de".equals(language)) {
            locale = Locale.GERMAN;
        }else if ("es".equals(language)) {
            locale = new Locale("es", "");
        } else if ("pt".equals(language)) {
            locale = new Locale("pt", "");
        } else if ("fr".equals(language)) {
            locale = new Locale("fr", "");
        } else if ("it".equals(language)) {
            locale = new Locale("it", "");
        } else if ("ja".equals(language)) {
            locale = new Locale("ja", "");
        }  else if ("ko".equals(language)) {
            locale = new Locale("ko", "");
        }else {
            locale =  Locale.ENGLISH;
        }
        Utils.createConfiguration(context, locale);
    }

    public static void createConfiguration(Context context, Locale locale) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();
            config.setLocale(locale);
            resources.updateConfiguration(config, dm);
        }
    }

    public static void saveLaun(Context context, String laun) {
        SharedPreferences sp = context.getSharedPreferences("LANJIN_LAUN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lj_video_laun", laun);
        editor.commit();
    }

    public static String getLaun(Context context) {
        SharedPreferences sp = context.getSharedPreferences("LANJIN_LAUN", Context.MODE_PRIVATE);
        return sp.getString("lj_video_laun",  Locale.getDefault().getLanguage());
    }
}

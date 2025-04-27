package com.app.yourvideoschannelapps.a;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;


import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class FireEvent {

    private static FireEvent instance;
    public static FireEvent getInstance(){
        if(instance==null)
            instance=new FireEvent();
        return instance;
    }
    private Context context;
    public void init(Context context){
        this.context=context;
        getAndroidId();
       getCountryCode();
    }
    public  void AdEvent(String event) {
//        Map<String, Object> eventValues = new HashMap<String, Object>();
//        eventValues.put(AFInAppEventParameterName.DEEP_LINK, System.currentTimeMillis());

        AppsFlyerLib.getInstance().logEvent(context, event, null, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                LogUtils.i("appfires event sent successfully");
            }

            @Override
            public void onError(int i, @NonNull String s) {
                LogUtils.i("appfires event failed to be sent:" +
                        "error code: " + i
                        + "error description: " + s);
            }
        });
    }

    @SuppressLint("HardwareIds")
    public  void getAndroidId() {
        Utils.ANDROID_ID = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        LogUtils.i("ANDROID_ID:"+Utils.ANDROID_ID);
    }

    public static final String ENDATA="uasydbwqiu@&(==sald51623233as@@++sadqwiu982--aslkdjilmeik56a4d564a1908=--=245456sd213a4d11-68a@@###";

    private static final String SECRET_KEY = "mnS=cr3tKey@2345"; // 16位
    private static final String INIT_VECTOR = "initV@ctor=12345"; // 16位
    public  String encrypt(){
        try {

        // 1. 解析公钥字符串
        IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal((ENDATA+ System.currentTimeMillis()+((int) (Math.random() * 900000) + 100000)).getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
        }catch (Exception e){

        }
        return "";
    }
    public  void getCountryCode(){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Utils.countryCode = telephonyManager.getNetworkCountryIso();
    }

    // SharedPreferences文件名
    private static final String PREFS_NAME = "Ffd_ISFIRST";
    // SharedPreferences中的键
    private static final String KEY_USER_NAME = "fdfirstKey";

    // 保存用户名到SharedPreferences
    public static void saveIsFirst(Context context,Boolean isFirst) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_USER_NAME, isFirst);
        editor.apply(); // 或者使用 editor.commit();
    }

    // 从SharedPreferences获取用户名
    public static boolean getFirst(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_USER_NAME, true); // 第二个参数是默认值
    }
}


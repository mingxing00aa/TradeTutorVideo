package com.app.yourvideoschannelapps.rests;

import androidx.annotation.NonNull;

import com.app.yourvideoschannelapps.BuildConfig;
import com.app.yourvideoschannelapps.a.FireEvent;
import com.app.yourvideoschannelapps.a.LogUtils;
import com.app.yourvideoschannelapps.a.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.HTTP;

@SuppressWarnings("WriteOnlyObject")
public class RestAdapter {

    public static ApiInterface createAPI(String api_url) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(api_url + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(ApiInterface.class);

    }
    public static ApiInterface createAPI2() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder requestBuilder = request.newBuilder();
                        HttpUrl url = request.url();
                        HttpUrl.Builder builder = url.newBuilder();
                        requestBuilder.url(builder.build())
                                .method(request.method(), request.body())
                                .addHeader("version", BuildConfig.VERSION_NAME)
                                .addHeader("phoneModel", "android")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("deviceNumber", Utils.ANDROID_ID)
                                .addHeader("Token", FireEvent.getInstance().encrypt())
                                .addHeader("countryCode",Utils.countryCode);
                        return chain.proceed(requestBuilder.build());
                    }
                })
                .cache(null)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_FORMAL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(ApiInterface.class);

    }
}


package com.willpower.editor.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * API初始化类
 */
public class RetrofitManager {
    private static RetrofitManager manager;
    private static final int TIMEOUT = 20;// 超时时间
    private Retrofit retrofit;

    public static synchronized RetrofitManager instance() {
        if (manager == null) {
            synchronized (RetrofitManager.class) {
                if (manager == null) {
                    manager = new RetrofitManager();
                }
            }
        }
        return manager;
    }

    private RetrofitManager() {
        init();
    }

    /**
     * 初始化必要对象和参数
     */
    void init() {
        // 初始化OKHttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置超时
        builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(addHttpLog());
        OkHttpClient client = builder.build();
        // 初始化Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Api.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
    }

    /***
     * 添加http请求log  包括请求url 请求参数  返回的参数 等信息。
     *
     * @return
     */
    HttpLoggingInterceptor addHttpLog() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    public Api request() {
        return retrofit.create(Api.class);
    }

}

package com.willpower.editor.http;

import com.willpower.log.Timber;

import java.io.IOException;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 为face++ 接口添加请求头信息
 */
public class FacePPInterceptor implements Interceptor {
    private static String boundaryString = getBoundary();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "multipart/form-data; boundary=" + boundaryString)
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)")
                .build();

        long t1 = System.currentTimeMillis();//请求发起的时间
        Timber.e("Request\n ", "\n url:" + request.url() +
                "\n connection:" + chain.connection() + "\n\n");
        Response response = chain.proceed(request);
        long t2 = System.currentTimeMillis();//收到响应的时间
        //不能直接使用response.body（）.string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，
        // 我们需要创建出一个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        Timber.e("Response\n ", "\n url:" + response.request().url() + "\n 响应码：" + response.code() +
                "\n 返回数据：" + responseBody.string() +
                "\n 耗时：" + (t2 - t1) + " ms\n\n");
        return response;
    }

    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".
                    charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_"
                            .length())));
        }
        return sb.toString();
    }
}

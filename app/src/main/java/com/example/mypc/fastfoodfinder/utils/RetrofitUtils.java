package com.example.mypc.fastfoodfinder.utils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class RetrofitUtils {
    public static Retrofit get(String apiKey)
    {
        return new Retrofit.Builder()
                .baseUrl(Constant.MAP_BASE_URL)
                .client(getClient(apiKey))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient getClient(String apiKey)
    {
        return new OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor(apiKey))
                .build();

    }

    private static Interceptor apiKeyInterceptor(final String apiKey)
    {
        return  new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                HttpUrl httpUrl = request.url()
                        .newBuilder()
                        .addQueryParameter("key", apiKey)
                        .build();

                request = request.newBuilder()
                        .url(httpUrl)
                        .build();

                return chain.proceed(request);
            }
        };
    }
}

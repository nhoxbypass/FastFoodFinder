@file:JvmName("RetrofitUtils")

package com.iceteaviet.fastfoodfinder.utils

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Genius Doan on 11/11/2016.
 */

fun get(apiKey: String, baseUrl: String): Retrofit {
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getClient(apiKey))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

private fun getClient(apiKey: String): OkHttpClient {
    return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor(apiKey))
            .build()

}

private fun apiKeyInterceptor(apiKey: String): Interceptor {
    return Interceptor { chain ->
        var request = chain.request()

        val httpUrl = request.url()
                .newBuilder()
                .addQueryParameter("key", apiKey)
                .build()

        request = request.newBuilder()
                .url(httpUrl)
                .build()

        chain.proceed(request)
    }
}
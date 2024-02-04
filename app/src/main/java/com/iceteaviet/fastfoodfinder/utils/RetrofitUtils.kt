@file:JvmName("RetrofitUtils")

package com.iceteaviet.fastfoodfinder.utils

import com.iceteaviet.fastfoodfinder.App
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Genius Doan on 11/11/2016.
 */

/**
 * Get Retrofit instance
 */
fun get(apiKey: String, baseUrl: String): Retrofit {
    val pkgName = App.getPackageName()
    val cert = App.getSignatureSHA1()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(getGoogleServicesClient(apiKey, pkgName, cert))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

/**
 * Get OkHttpClient to construct Retrofit
 */
private fun getGoogleServicesClient(apiKey: String, pkgName: String, cert: String): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(getGoogleServicesInterceptor(apiKey, pkgName, cert))
        .build()

}

/**
 * Return an Http Interceptor which contain api key
 */
private fun getGoogleServicesInterceptor(apiKey: String, pkgName: String, cert: String): Interceptor {
    return Interceptor { chain ->
        var request = chain.request()

        val httpUrl = request.url()
            .newBuilder()
            .addQueryParameter("key", apiKey)
            .build()

        request = request.newBuilder()
            .header("X-Android-Package", pkgName)
            .header("X-Android-Cert", cert) // SHA-1
            .url(httpUrl)
            .build()

        chain.proceed(request)
    }
}
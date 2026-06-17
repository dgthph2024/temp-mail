package com.tempmail.app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.internal.temp-mail.io/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Host", "api.internal.temp-mail.io")
                .addHeader("authority", "api.internal.temp-mail.io")
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("accept-language", "en-US,en;q=0.9,vi;q=0.8")
                .addHeader("application-name", "web")
                .addHeader("application-version", "2.2.29")
                .addHeader("origin", "https://temp-mail.io")
                .addHeader("referer", "https://temp-mail.io/")
                .addHeader("user-agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.5993.80 Mobile Safari/537.36")
                .build()
            chain.proceed(request)
        }
        .build()

    val instance: TempMailApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TempMailApi::class.java)
    }
}

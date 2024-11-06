package com.overdevx.reservationapp.data.module

import android.content.Context
import android.content.SharedPreferences
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.data.remote.ApiService2
import com.overdevx.reservationapp.utils.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.inject.Qualifier


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    var BASE_URL ="http://192.168.1.110:3000/api/"
    var BASE_URL2 ="http://192.168.39.85:3000/api/"
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenProvider: TokenProvider): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val token = tokenProvider.getToken() // Ambil token dari tokenProvider

                // Buat request baru dengan header Authorization
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $token") // Tambahkan token ke dalam header
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // Tambahkan OkHttpClient
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

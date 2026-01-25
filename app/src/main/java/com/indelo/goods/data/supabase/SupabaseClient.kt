package com.indelo.goods.data.supabase

import com.indelo.goods.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Supabase API client using Retrofit for REST API calls
 */
object SupabaseClientProvider {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SupabaseConfig.SUPABASE_URL.let { if (it.endsWith("/")) it else "$it/" })
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: SupabaseApiService = retrofit.create(SupabaseApiService::class.java)

    /**
     * Session management - stores access token after authentication
     */
    object Session {
        private var accessToken: String? = null
        private var refreshToken: String? = null
        private var user: User? = null

        fun setSession(authResponse: AuthResponse) {
            accessToken = authResponse.access_token
            refreshToken = authResponse.refresh_token
            user = authResponse.user
        }

        fun getAccessToken(): String? = accessToken

        fun getUser(): User? = user

        fun clearSession() {
            accessToken = null
            refreshToken = null
            user = null
        }

        fun isAuthenticated(): Boolean = accessToken != null && user != null

        fun getBearerToken(): String? = accessToken?.let { "Bearer $it" }
    }
}

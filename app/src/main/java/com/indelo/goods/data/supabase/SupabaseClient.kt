package com.indelo.goods.data.supabase

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
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

    private var appContext: Context? = null
    private val gson = Gson()

    fun initialize(context: Context) {
        appContext = context.applicationContext
        Session.loadSession()
    }

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
            val requestBuilder = original.newBuilder()
                .header("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")

            // Add Authorization header if user is authenticated
            Session.getBearerToken()?.let { token ->
                requestBuilder.header("Authorization", token)
                android.util.Log.d("SupabaseClient", "Adding Authorization header to request")
            }

            val request = requestBuilder
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
     * Persists to SharedPreferences for background/app restart persistence
     */
    object Session {
        private const val PREFS_NAME = "supabase_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER = "user"

        private var accessToken: String? = null
        private var refreshToken: String? = null
        private var user: User? = null

        private fun getPrefs(): SharedPreferences? {
            return appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        fun loadSession() {
            val prefs = getPrefs() ?: return
            accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
            refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null)
            val userJson = prefs.getString(KEY_USER, null)
            user = userJson?.let {
                try {
                    gson.fromJson(it, User::class.java)
                } catch (e: Exception) {
                    android.util.Log.e("SupabaseClient", "Failed to parse user from prefs", e)
                    null
                }
            }
            android.util.Log.d("SupabaseClient", "Loaded session: hasToken=${accessToken != null}, hasUser=${user != null}")
        }

        fun setSession(authResponse: AuthResponse) {
            accessToken = authResponse.access_token
            refreshToken = authResponse.refresh_token
            user = authResponse.user

            // Persist to SharedPreferences
            val prefs = getPrefs() ?: return
            prefs.edit().apply {
                putString(KEY_ACCESS_TOKEN, accessToken)
                putString(KEY_REFRESH_TOKEN, refreshToken)
                user?.let { putString(KEY_USER, gson.toJson(it)) }
                apply()
            }
            android.util.Log.d("SupabaseClient", "Saved session to prefs")
        }

        fun getAccessToken(): String? = accessToken

        fun getUser(): User? = user

        fun clearSession() {
            accessToken = null
            refreshToken = null
            user = null

            // Clear from SharedPreferences
            getPrefs()?.edit()?.clear()?.apply()
            android.util.Log.d("SupabaseClient", "Cleared session from prefs")
        }

        fun isAuthenticated(): Boolean = accessToken != null && user != null

        fun getBearerToken(): String? = accessToken?.let { "Bearer $it" }
    }
}

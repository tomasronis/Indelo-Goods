package com.indelo.goods.data.supabase

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit service interface for Supabase REST API
 * Documentation: https://supabase.com/docs/reference/rest
 */
interface SupabaseApiService {

    // ============ Auth API ============

    @POST("/auth/v1/signup")
    suspend fun signUp(@Body body: JsonObject): Response<AuthResponse>

    @POST("/auth/v1/token?grant_type=password")
    suspend fun signIn(@Body body: JsonObject): Response<AuthResponse>

    @POST("/auth/v1/otp")
    suspend fun sendOtp(@Body body: JsonObject): Response<JsonObject>

    @POST("/auth/v1/verify")
    suspend fun verifyOtp(@Body body: JsonObject): Response<AuthResponse>

    @POST("/auth/v1/recover")
    suspend fun resetPassword(@Body body: JsonObject): Response<JsonObject>

    @POST("/auth/v1/logout")
    suspend fun signOut(): Response<Unit>

    @GET("/auth/v1/user")
    suspend fun getUser(@Header("Authorization") token: String): Response<User>

    // ============ Database API (PostgREST) ============

    @GET("/rest/v1/{table}")
    suspend fun select(
        @Path("table") table: String,
        @Query("select") columns: String = "*",
        @QueryMap filters: Map<String, String> = emptyMap()
    ): Response<List<JsonObject>>

    @POST("/rest/v1/{table}")
    suspend fun insert(
        @Path("table") table: String,
        @Body body: Any,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<JsonObject>>

    @PATCH("/rest/v1/{table}")
    suspend fun update(
        @Path("table") table: String,
        @Body body: Any,
        @QueryMap filters: Map<String, String>,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<JsonObject>>

    @HTTP(method = "DELETE", path = "/rest/v1/{table}", hasBody = false)
    suspend fun delete(
        @Path("table") table: String,
        @QueryMap filters: Map<String, String>
    ): Response<Unit>

    // ============ Storage API ============

    @POST("/storage/v1/object/{bucket}/{path}")
    suspend fun uploadFile(
        @Path("bucket") bucket: String,
        @Path("path") path: String,
        @Body file: okhttp3.RequestBody
    ): Response<StorageUploadResponse>

    @DELETE("/storage/v1/object/{bucket}/{path}")
    suspend fun deleteFile(
        @Path("bucket") bucket: String,
        @Path("path") path: String
    ): Response<Unit>
}

// ============ Response Models ============

data class AuthResponse(
    val access_token: String?,
    val refresh_token: String?,
    val user: User?,
    val expires_in: Int?
)

data class User(
    val id: String,
    val email: String?,
    val phone: String?,
    val role: String?,
    val created_at: String?
)

data class StorageUploadResponse(
    val Key: String?,
    val key: String?
)

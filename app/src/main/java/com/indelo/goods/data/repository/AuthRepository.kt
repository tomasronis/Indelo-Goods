package com.indelo.goods.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.indelo.goods.data.model.UserProfile
import com.indelo.goods.data.model.UserType
import com.indelo.goods.data.supabase.SupabaseClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepository {

    private val api = SupabaseClientProvider.api
    private val session = SupabaseClientProvider.Session
    private val gson = Gson()

    // Session status flow
    private val _sessionStatus = MutableStateFlow(session.isAuthenticated())
    val isAuthenticated: Flow<Boolean> = _sessionStatus

    val sessionStatus: Flow<Boolean> = _sessionStatus

    init {
        // Update session status on initialization (after loading from prefs)
        _sessionStatus.value = session.isAuthenticated()
        android.util.Log.d("AuthRepository", "Initialized with auth status: ${_sessionStatus.value}")
    }

    val currentUserId: String?
        get() = session.getUser()?.id

    val currentUserEmail: String?
        get() = session.getUser()?.email

    val currentUserPhone: String?
        get() = session.getUser()?.phone

    // Phone OTP - Send code
    suspend fun sendOtp(phone: String): Result<Unit> {
        return try {
            val body = JsonObject().apply {
                addProperty("phone", phone)
            }
            android.util.Log.d("AuthRepository", "Sending OTP to: $phone")
            val response = api.sendOtp(body)
            android.util.Log.d("AuthRepository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful) {
                android.util.Log.d("AuthRepository", "OTP sent successfully")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                android.util.Log.e("AuthRepository", "OTP failed: $errorBody")
                Result.failure(Exception("Failed to send OTP: $errorBody"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "OTP exception", e)
            Result.failure(e)
        }
    }

    // Phone OTP - Verify code
    suspend fun verifyOtp(phone: String, token: String): Result<Unit> {
        return try {
            val body = JsonObject().apply {
                addProperty("phone", phone)
                addProperty("token", token)
                addProperty("type", "sms")
            }
            android.util.Log.d("AuthRepository", "Verifying OTP for: $phone, token: $token")
            val response = api.verifyOtp(body)
            android.util.Log.d("AuthRepository", "Verify response code: ${response.code()}, isSuccessful: ${response.isSuccessful}, hasBody: ${response.body() != null}")
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                android.util.Log.d("AuthRepository", "Auth response: userId=${authResponse.user?.id}, hasAccessToken=${authResponse.access_token != null}")
                session.setSession(authResponse)
                _sessionStatus.value = true
                android.util.Log.d("AuthRepository", "Session set, isAuthenticated=${session.isAuthenticated()}")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                android.util.Log.e("AuthRepository", "Verify OTP failed: $errorBody")
                Result.failure(Exception("Failed to verify OTP: $errorBody"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Verify OTP exception", e)
            Result.failure(e)
        }
    }

    // Email/password sign up (fallback)
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            val body = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }
            val response = api.signUp(body)
            if (response.isSuccessful && response.body() != null) {
                session.setSession(response.body()!!)
                _sessionStatus.value = true
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to sign up: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Email/password sign in (fallback)
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val body = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }
            val response = api.signIn(body)
            if (response.isSuccessful && response.body() != null) {
                session.setSession(response.body()!!)
                _sessionStatus.value = true
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to sign in: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            api.signOut()
            session.clearSession()
            _sessionStatus.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            val body = JsonObject().apply {
                addProperty("email", email)
            }
            val response = api.resetPassword(body)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to reset password: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Saves the user type to the user_profiles table in Supabase
     */
    suspend fun saveUserType(userType: UserType): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = currentUserId
            android.util.Log.d("AuthRepository", "Saving user type: $userType for userId: $userId")

            if (userId == null) {
                android.util.Log.e("AuthRepository", "Cannot save user type - user not authenticated")
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            val profile = UserProfile(
                id = userId,
                userType = userType.name
            )

            android.util.Log.d("AuthRepository", "Inserting profile: $profile")
            val response = api.insert(
                table = "user_profiles",
                body = profile
            )

            android.util.Log.d("AuthRepository", "Insert response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                android.util.Log.d("AuthRepository", "User type saved successfully")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                android.util.Log.e("AuthRepository", "Failed to save user type: $errorBody")
                Result.failure(Exception("Failed to save user type: $errorBody"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Exception saving user type", e)
            Result.failure(e)
        }
    }

    /**
     * Gets the user type from the user_profiles table in Supabase
     */
    suspend fun getUserType(): Result<UserType?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = currentUserId
            android.util.Log.d("AuthRepository", "Getting user type for userId: $userId")
            if (userId == null) {
                android.util.Log.d("AuthRepository", "No userId, returning null")
                return@withContext Result.success(null)
            }

            val filters = mapOf("id" to "eq.$userId")
            val response = api.select(
                table = "user_profiles",
                filters = filters
            )

            android.util.Log.d("AuthRepository", "User profile response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val profiles = response.body() ?: emptyList()
                android.util.Log.d("AuthRepository", "Found ${profiles.size} user profiles")
                val profile = profiles.firstOrNull()?.let {
                    gson.fromJson(it, UserProfile::class.java)
                }
                val userType = profile?.userType?.let { UserType.valueOf(it) }
                android.util.Log.d("AuthRepository", "User type: $userType")
                Result.success(userType)
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                android.util.Log.e("AuthRepository", "Failed to get user type: $errorBody")
                Result.failure(Exception("Failed to get user type: $errorBody"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Get user type exception", e)
            Result.failure(e)
        }
    }
}

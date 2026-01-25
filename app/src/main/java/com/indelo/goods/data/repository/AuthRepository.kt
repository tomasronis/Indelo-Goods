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
            val response = api.sendOtp(body)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to send OTP: ${response.message()}"))
            }
        } catch (e: Exception) {
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
            val response = api.verifyOtp(body)
            if (response.isSuccessful && response.body() != null) {
                session.setSession(response.body()!!)
                _sessionStatus.value = true
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to verify OTP: ${response.message()}"))
            }
        } catch (e: Exception) {
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
            val userId = currentUserId ?: return@withContext Result.failure(Exception("User not authenticated"))

            val profile = UserProfile(
                id = userId,
                userType = userType.name
            )

            val response = api.insert(
                table = "user_profiles",
                body = profile
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to save user type: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets the user type from the user_profiles table in Supabase
     */
    suspend fun getUserType(): Result<UserType?> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = currentUserId ?: return@withContext Result.success(null)

            val filters = mapOf("id" to "eq.$userId")
            val response = api.select(
                table = "user_profiles",
                filters = filters
            )

            if (response.isSuccessful) {
                val profiles = response.body() ?: emptyList()
                val profile = profiles.firstOrNull()?.let {
                    gson.fromJson(it, UserProfile::class.java)
                }
                val userType = profile?.userType?.let { UserType.valueOf(it) }
                Result.success(userType)
            } else {
                Result.failure(Exception("Failed to get user type: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

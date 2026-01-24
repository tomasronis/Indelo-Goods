package com.indelo.goods.data.repository

import com.indelo.goods.data.model.UserProfile
import com.indelo.goods.data.model.UserType
import com.indelo.goods.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.Phone
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepository {

    private val auth = SupabaseClientProvider.client.auth
    private val postgrest = SupabaseClientProvider.client.postgrest

    val isAuthenticated: Flow<Boolean> = auth.sessionStatus.map { status ->
        status is SessionStatus.Authenticated
    }

    val sessionStatus: Flow<SessionStatus> = auth.sessionStatus

    val currentUserId: String?
        get() = auth.currentUserOrNull()?.id

    val currentUserEmail: String?
        get() = auth.currentUserOrNull()?.email

    val currentUserPhone: String?
        get() = auth.currentUserOrNull()?.phone

    // Phone OTP - Send code
    suspend fun sendOtp(phone: String): Result<Unit> {
        return try {
            auth.signInWith(Phone) {
                this.phone = phone
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Phone OTP - Verify code
    suspend fun verifyOtp(phone: String, token: String): Result<Unit> {
        return try {
            auth.verifyPhoneOtp(
                type = Phone.OtpType.SMS,
                phone = phone,
                token = token
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Email/password sign up (fallback)
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Email/password sign in (fallback)
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.resetPasswordForEmail(email)
            Result.success(Unit)
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

            // Upsert the profile (insert or update if exists)
            postgrest
                .from("user_profiles")
                .upsert(profile)

            Result.success(Unit)
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

            val profile = postgrest
                .from("user_profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<UserProfile>()

            val userType = profile?.userType?.let { UserType.valueOf(it) }
            Result.success(userType)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

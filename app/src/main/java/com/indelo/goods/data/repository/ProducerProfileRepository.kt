package com.indelo.goods.data.repository

import com.google.gson.Gson
import com.indelo.goods.data.model.ProducerProfile
import com.indelo.goods.data.supabase.SupabaseClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProducerProfileRepository {

    private val api = SupabaseClientProvider.api
    private val gson = Gson()

    suspend fun getProducerProfile(producerId: String): Result<ProducerProfile?> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$producerId")
            val response = api.select(table = "producer_profiles", filters = filters)
            if (response.isSuccessful) {
                val profile = response.body()?.firstOrNull()?.let {
                    gson.fromJson(it, ProducerProfile::class.java)
                }
                Result.success(profile)
            } else {
                Result.failure(Exception("Failed to get producer profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProducerProfile(profile: ProducerProfile): Result<ProducerProfile> = withContext(Dispatchers.IO) {
        try {
            val response = api.insert(table = "producer_profiles", body = profile)
            if (response.isSuccessful) {
                val createdProfile = response.body()?.firstOrNull()?.let {
                    gson.fromJson(it, ProducerProfile::class.java)
                }
                if (createdProfile != null) {
                    Result.success(createdProfile)
                } else {
                    Result.failure(Exception("Failed to parse created profile"))
                }
            } else {
                Result.failure(Exception("Failed to create producer profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProducerProfile(profile: ProducerProfile): Result<ProducerProfile> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.${profile.id!!}")
            val response = api.update(table = "producer_profiles", body = profile, filters = filters)
            if (response.isSuccessful) {
                val updatedProfile = response.body()?.firstOrNull()?.let {
                    gson.fromJson(it, ProducerProfile::class.java)
                }
                if (updatedProfile != null) {
                    Result.success(updatedProfile)
                } else {
                    Result.failure(Exception("Failed to parse updated profile"))
                }
            } else {
                Result.failure(Exception("Failed to update producer profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProducerProfile(profileId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val filters = mapOf("id" to "eq.$profileId")
            val response = api.delete(table = "producer_profiles", filters = filters)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete producer profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

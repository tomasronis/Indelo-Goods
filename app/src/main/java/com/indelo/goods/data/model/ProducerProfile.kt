package com.indelo.goods.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName

@Serializable
data class ProducerProfile(
    val id: String? = null,

    @SerialName("company_name")
    @SerializedName("company_name")
    val companyName: String? = null,

    @SerialName("brand_name")
    @SerializedName("brand_name")
    val brandName: String? = null,

    val bio: String? = null,
    val background: String? = null,
    val inspiration: String? = null,
    val goals: String? = null,

    @SerialName("website_url")
    @SerializedName("website_url")
    val websiteUrl: String? = null,

    @SerialName("logo_url")
    @SerializedName("logo_url")
    val logoUrl: String? = null,

    @SerialName("cover_image_url")
    @SerializedName("cover_image_url")
    val coverImageUrl: String? = null,

    val location: String? = null,

    @SerialName("founded_year")
    @SerializedName("founded_year")
    val foundedYear: Int? = null,

    val specialty: String? = null,
    val certifications: List<String>? = null,

    @SerialName("created_at")
    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

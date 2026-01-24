package com.indelo.goods.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String? = null,

    // Basic Info
    val name: String,
    val brand: String? = null,
    val description: String? = null,
    @SerialName("short_description")
    val shortDescription: String? = null,

    // Pricing
    @SerialName("wholesale_price")
    val wholesalePrice: Double,          // Price for shops
    @SerialName("retail_price")
    val retailPrice: Double? = null,     // Suggested retail price
    val currency: String = "USD",

    // Product Specifications
    @SerialName("volume_ml")
    val volumeMl: Int? = null,           // Volume in milliliters
    @SerialName("weight_g")
    val weightG: Int? = null,            // Weight in grams
    @SerialName("serving_size")
    val servingSize: String? = null,     // e.g., "240ml", "1 can"
    @SerialName("servings_per_container")
    val servingsPerContainer: Int? = null,

    // Packaging
    @SerialName("units_per_case")
    val unitsPerCase: Int = 1,           // How many units in a wholesale case
    @SerialName("case_dimensions")
    val caseDimensions: String? = null,  // e.g., "12x8x6 inches"
    @SerialName("case_weight_kg")
    val caseWeightKg: Double? = null,

    // Ingredients & Nutrition
    val ingredients: String? = null,     // Comma-separated or full list
    @SerialName("nutrition_facts")
    val nutritionFacts: NutritionFacts? = null,
    val allergens: String? = null,       // e.g., "Contains: Soy, Wheat"

    // Certifications
    @SerialName("is_organic")
    val isOrganic: Boolean = false,
    @SerialName("is_non_gmo")
    val isNonGmo: Boolean = false,
    @SerialName("is_vegan")
    val isVegan: Boolean = false,
    @SerialName("is_gluten_free")
    val isGlutenFree: Boolean = false,
    @SerialName("is_kosher")
    val isKosher: Boolean = false,
    @SerialName("other_certifications")
    val otherCertifications: String? = null,

    // Inventory & Ordering
    val sku: String? = null,             // Stock Keeping Unit
    val upc: String? = null,             // Universal Product Code (barcode)
    @SerialName("minimum_order_quantity")
    val minimumOrderQuantity: Int = 1,
    @SerialName("lead_time_days")
    val leadTimeDays: Int? = null,       // Days to fulfill order
    @SerialName("in_stock")
    val inStock: Boolean = true,
    @SerialName("stock_quantity")
    val stockQuantity: Int? = null,

    // Media
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("additional_images")
    val additionalImages: List<String>? = null,

    // Categorization
    @SerialName("category_id")
    val categoryId: String? = null,
    val tags: List<String>? = null,      // e.g., ["beverage", "sparkling", "craft"]

    // Producer Info
    @SerialName("producer_id")
    val producerId: String? = null,
    @SerialName("country_of_origin")
    val countryOfOrigin: String? = null,
    @SerialName("shelf_life_days")
    val shelfLifeDays: Int? = null,
    @SerialName("storage_instructions")
    val storageInstructions: String? = null,

    // Metadata
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class NutritionFacts(
    val calories: Int? = null,
    @SerialName("total_fat_g")
    val totalFatG: Double? = null,
    @SerialName("saturated_fat_g")
    val saturatedFatG: Double? = null,
    @SerialName("trans_fat_g")
    val transFatG: Double? = null,
    @SerialName("cholesterol_mg")
    val cholesterolMg: Int? = null,
    @SerialName("sodium_mg")
    val sodiumMg: Int? = null,
    @SerialName("total_carbs_g")
    val totalCarbsG: Double? = null,
    @SerialName("dietary_fiber_g")
    val dietaryFiberG: Double? = null,
    @SerialName("total_sugars_g")
    val totalSugarsG: Double? = null,
    @SerialName("added_sugars_g")
    val addedSugarsG: Double? = null,
    @SerialName("protein_g")
    val proteinG: Double? = null,
    @SerialName("vitamin_d_mcg")
    val vitaminDMcg: Double? = null,
    @SerialName("calcium_mg")
    val calciumMg: Int? = null,
    @SerialName("iron_mg")
    val ironMg: Double? = null,
    @SerialName("potassium_mg")
    val potassiumMg: Int? = null
)

@Serializable
data class Category(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    @SerialName("parent_id")
    val parentId: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Predefined categories for canned food/beverages
object ProductCategories {
    val CANNED_VEGETABLES = "Canned Vegetables"
    val CANNED_FRUITS = "Canned Fruits"
    val CANNED_BEANS = "Canned Beans & Legumes"
    val CANNED_SOUPS = "Soups & Broths"
    val CANNED_MEATS = "Canned Meats & Seafood"
    val SAUCES = "Sauces & Condiments"
    val BEVERAGES_SODA = "Sodas & Soft Drinks"
    val BEVERAGES_JUICE = "Juices"
    val BEVERAGES_ENERGY = "Energy Drinks"
    val BEVERAGES_SPARKLING = "Sparkling Water"
    val BEVERAGES_TEA_COFFEE = "Ready-to-Drink Tea & Coffee"
    val BEVERAGES_CRAFT = "Craft Beverages"
    val PICKLED = "Pickled & Fermented"
    val OTHER = "Other"
}

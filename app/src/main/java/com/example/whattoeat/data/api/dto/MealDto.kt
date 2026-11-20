package com.example.whattoeat.data.api.dto

import com.google.gson.annotations.SerializedName

data class MealDbResponse(
    @SerializedName("meals")
    val meals: List<MealSummary>? = emptyList()
)

data class MealDetailResponse(
    @SerializedName("meals")
    val meals: List<MealDetail>? = emptyList()
)

data class MealSummary(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strMealThumb")
    val strMealThumb: String
)

data class MealDetail(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strDrinkAlternate")
    val strDrinkAlternate: String? = null,
    @SerializedName("strCategory")
    val strCategory: String? = null,
    @SerializedName("strArea")
    val strArea: String? = null,
    @SerializedName("strInstructions")
    val strInstructions: String? = null,
    @SerializedName("strMealThumb")
    val strMealThumb: String? = null,
    @SerializedName("strTags")
    val strTags: String? = null,
    @SerializedName("strYoutube")
    val strYoutube: String? = null,
    // Only essential ingredient fields to avoid KAPT issues
    @SerializedName("strIngredient1")
    val strIngredient1: String? = null,
    @SerializedName("strIngredient2")
    val strIngredient2: String? = null,
    @SerializedName("strIngredient3")
    val strIngredient3: String? = null,
    @SerializedName("strIngredient4")
    val strIngredient4: String? = null,
    @SerializedName("strIngredient5")
    val strIngredient5: String? = null,
    @SerializedName("strIngredient6")
    val strIngredient6: String? = null,
    @SerializedName("strIngredient7")
    val strIngredient7: String? = null,
    @SerializedName("strIngredient8")
    val strIngredient8: String? = null,
    @SerializedName("strIngredient9")
    val strIngredient9: String? = null,
    @SerializedName("strIngredient10")
    val strIngredient10: String? = null,
    @SerializedName("strMeasure1")
    val strMeasure1: String? = null,
    @SerializedName("strMeasure2")
    val strMeasure2: String? = null,
    @SerializedName("strMeasure3")
    val strMeasure3: String? = null,
    @SerializedName("strMeasure4")
    val strMeasure4: String? = null,
    @SerializedName("strMeasure5")
    val strMeasure5: String? = null,
    @SerializedName("strMeasure6")
    val strMeasure6: String? = null,
    @SerializedName("strMeasure7")
    val strMeasure7: String? = null,
    @SerializedName("strMeasure8")
    val strMeasure8: String? = null,
    @SerializedName("strMeasure9")
    val strMeasure9: String? = null,
    @SerializedName("strMeasure10")
    val strMeasure10: String? = null,
    @SerializedName("strSource")
    val strSource: String? = null,
    @SerializedName("strImageSource")
    val strImageSource: String? = null,
    @SerializedName("strCreativeCommonsConfirmed")
    val strCreativeCommonsConfirmed: String? = null,
    @SerializedName("dateModified")
    val dateModified: String? = null
) {
    fun ingredients(): List<Pair<String, String>> {
        val ingredients = mutableListOf<Pair<String, String>>()
        val ingredientFields = listOf(
            strIngredient1 to strMeasure1,
            strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3,
            strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5,
            strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7,
            strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9,
            strIngredient10 to strMeasure10
        )
        for ((ingredient, measure) in ingredientFields) {
            if (!ingredient.isNullOrBlank()) {
                ingredients.add(ingredient.trim() to measure?.trim() ?: "")
            }
        }
        return ingredients
    }

    fun instructionSteps(): List<String> {
        return strInstructions?.split("\r\n")
            .orEmpty()
            .filter { it.isNotBlank() }
            .map { it.trim() }
            ?: emptyList()
    }

    fun tagsList(): List<String> {
        return strTags?.split(",")
            .orEmpty()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            ?: emptyList()
    }
}

data class CategoryListResponse(
    @SerializedName("meals")
    val meals: List<CategoryDto>? = emptyList()
)

data class CategoryDto(
    @SerializedName("idCategory")
    val idCategory: String,
    @SerializedName("strCategory")
    val strCategory: String,
    @SerializedName("strCategoryThumb")
    val strCategoryThumb: String,
    @SerializedName("strCategoryDescription")
    val strCategoryDescription: String
)

data class AreaListResponse(
    @SerializedName("meals")
    val meals: List<AreaDto>? = emptyList()
)

data class AreaDto(
    @SerializedName("strArea")
    val strArea: String
)

data class IngredientListResponse(
    @SerializedName("meals")
    val meals: List<IngredientDto>? = emptyList()
)

data class IngredientDto(
    @SerializedName("idIngredient")
    val idIngredient: String,
    @SerializedName("strIngredient")
    val strIngredient: String,
    @SerializedName("strDescription")
    val strDescription: String? = null,
    @SerializedName("strType")
    val strType: String? = null
)
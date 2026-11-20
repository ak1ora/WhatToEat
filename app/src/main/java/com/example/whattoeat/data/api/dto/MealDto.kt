package com.example.whattoeat.data.api.dto

data class MealDbResponse(
    val meals: List<MealSummary>? = emptyList()
)

data class MealDetailResponse(
    val meals: List<MealDetail>? = emptyList()
)

data class MealSummary(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String
)

data class MealDetail(
    val idMeal: String,
    val strMeal: String,
    val strDrinkAlternate: String? = null,
    val strCategory: String? = null,
    val strArea: String? = null,
    val strInstructions: String? = null,
    val strMealThumb: String? = null,
    val strTags: String? = null,
    val strYoutube: String? = null,
    val strIngredient1: String? = null,
    val strIngredient2: String? = null,
    val strIngredient3: String? = null,
    val strIngredient4: String? = null,
    val strIngredient5: String? = null,
    val strIngredient6: String? = null,
    val strIngredient7: String? = null,
    val strIngredient8: String? = null,
    val strIngredient9: String? = null,
    val strIngredient10: String? = null,
    val strMeasure1: String? = null,
    val strMeasure2: String? = null,
    val strMeasure3: String? = null,
    val strMeasure4: String? = null,
    val strMeasure5: String? = null,
    val strMeasure6: String? = null,
    val strMeasure7: String? = null,
    val strMeasure8: String? = null,
    val strMeasure9: String? = null,
    val strMeasure10: String? = null,
    val strSource: String? = null,
    val strImageSource: String? = null,
    val strCreativeCommonsConfirmed: String? = null,
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
    val meals: List<CategoryDto>? = emptyList()
)

data class CategoryDto(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String,
    val strCategoryDescription: String
)

data class AreaListResponse(
    val meals: List<AreaDto>? = emptyList()
)

data class AreaDto(
    val strArea: String
)

data class IngredientListResponse(
    val meals: List<IngredientDto>? = emptyList()
)

data class IngredientDto(
    val idIngredient: String,
    val strIngredient: String,
    val strDescription: String? = null,
    val strType: String? = null
)
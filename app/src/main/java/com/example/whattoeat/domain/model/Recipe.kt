package com.example.whattoeat.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String,
    val title: String,
    val imageUrl: String,
    val category: String?,
    val area: String?,
    val instructions: String,
    val ingredients: List<Ingredient>,
    val tags: List<String>,
    val youtubeUrl: String?,
    val sourceUrl: String?,
    val dateModified: String?
) {
    val instructionSteps: List<String>
        get() = instructions.split("\n").filter { it.isNotBlank() }.map { it.trim() }

    val timeMinutes: Int
        get() = estimateCookingTime()

    val difficulty: Difficulty
        get() = estimateDifficulty()

    private fun estimateCookingTime(): Int {
        // Rough estimation based on ingredient count and steps
        val baseTime = ingredients.size * 2 + instructionSteps.size * 3
        return baseTime.coerceIn(10, 120)
    }

    private fun estimateDifficulty(): Difficulty {
        val complexity = ingredients.size + instructionSteps.size
        return when {
            complexity <= 10 -> Difficulty.Easy
            complexity <= 20 -> Difficulty.Medium
            else -> Difficulty.Hard
        }
    }
}

@Serializable
data class Ingredient(
    val name: String,
    val measure: String,
    val isOptional: Boolean = false
)

enum class Difficulty {
    Easy,    // Очень просто
    Medium,  // Просто
    Hard     // Средне
}

@Serializable
data class Nutrients(
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbohydrates: Double? = null,
    val fiber: Double? = null,
    val sugar: Double? = null,
    val sodium: Double? = null
)

@Serializable
data class UserPreferences(
    val mealType: MealType? = null,
    val maxTimeMinutes: Int? = null,
    val difficulty: Difficulty? = null,
    val cuisine: String? = null,
    val availableIngredients: List<String> = emptyList(),
    val excludedIngredients: List<String> = emptyList(),
    val diet: Diet? = null,
    val maxCalories: Int? = null,
    val minProtein: Double? = null
) {
    fun copyWith(
        mealType: MealType? = this.mealType,
        maxTimeMinutes: Int? = this.maxTimeMinutes,
        difficulty: Difficulty? = this.difficulty,
        cuisine: String? = this.cuisine,
        availableIngredients: List<String> = this.availableIngredients,
        excludedIngredients: List<String> = this.excludedIngredients,
        diet: Diet? = this.diet,
        maxCalories: Int? = this.maxCalories,
        minProtein: Double? = this.minProtein
    ) = UserPreferences(
        mealType = mealType,
        maxTimeMinutes = maxTimeMinutes,
        difficulty = difficulty,
        cuisine = cuisine,
        availableIngredients = availableIngredients,
        excludedIngredients = excludedIngredients,
        diet = diet,
        maxCalories = maxCalories,
        minProtein = minProtein
    )
}

enum class MealType {
    Breakfast,   // Завтрак
    Lunch,       // Обед
    Dinner,      // Ужин
    Snack        // Перекус
}

enum class Diet {
    None,           // Без ограничений
    Vegetarian,     // Вегетарианское
    Vegan,          // Веганское
    Keto,           // Кето
    LowCarb,        // Низкоуглеводное
    GlutenFree,     // Без глютена
    DairyFree,      // Без лактозы
    HighProtein     // Высокобелковое
}
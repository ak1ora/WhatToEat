package com.example.whattoeat.data.mapper

import com.example.whattoeat.data.api.dto.MealDetail
import com.example.whattoeat.data.api.dto.MealSummary
import com.example.whattoeat.domain.model.Difficulty
import com.example.whattoeat.domain.model.Ingredient
import com.example.whattoeat.domain.model.Recipe

object RecipeMapper {

    fun toDomain(meal: MealDetail): Recipe {
        val ingredients = meal.ingredients().map { (name, measure) ->
            Ingredient(name = name, measure = measure)
        }

        return Recipe(
            id = meal.idMeal,
            title = meal.strMeal,
            imageUrl = meal.strMealThumb ?: "",
            category = meal.strCategory,
            area = meal.strArea,
            instructions = meal.strInstructions ?: "",
            ingredients = ingredients,
            tags = meal.tagsList(),
            youtubeUrl = meal.strYoutube,
            sourceUrl = meal.strSource,
            dateModified = meal.dateModified
        )
    }

    fun toDomainSummary(meal: MealSummary): Recipe {
        return Recipe(
            id = meal.idMeal,
            title = meal.strMeal,
            imageUrl = meal.strMealThumb,
            category = null,
            area = null,
            instructions = "",
            ingredients = emptyList(),
            tags = emptyList(),
            youtubeUrl = null,
            sourceUrl = null,
            dateModified = null
        )
    }

    fun estimateDifficulty(ingredientsCount: Int, stepsCount: Int): Difficulty {
        val complexity = ingredientsCount + stepsCount
        return when {
            complexity <= 10 -> Difficulty.Easy
            complexity <= 20 -> Difficulty.Medium
            else -> Difficulty.Hard
        }
    }

    fun estimateTimeMinutes(ingredientsCount: Int, stepsCount: Int): Int {
        val baseTime = ingredientsCount * 2 + stepsCount * 3
        return baseTime.coerceIn(10, 120)
    }
}
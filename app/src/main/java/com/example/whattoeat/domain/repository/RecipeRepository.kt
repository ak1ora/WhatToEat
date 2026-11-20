package com.example.whattoeat.domain.repository

import com.example.whattoeat.domain.model.Recipe
import com.example.whattoeat.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchRecipes(preferences: UserPreferences): Result<List<Recipe>>
    suspend fun getRecipeById(id: String): Result<Recipe>
    suspend fun getRandomRecipes(count: Int): Result<List<Recipe>>
    suspend fun getCategories(): Result<List<String>>
    suspend fun getAreas(): Result<List<String>>
    suspend fun getIngredients(): Result<List<String>>

    // Favorites
    suspend fun addToFavorites(recipe: Recipe)
    suspend fun removeFromFavorites(recipeId: String)
    fun getFavorites(): Flow<List<Recipe>>
    suspend fun isFavorite(recipeId: String): Boolean

    // History
    suspend fun addToHistory(recipe: Recipe)
    fun getHistory(): Flow<List<Recipe>>
    suspend fun clearHistory()

    // Cache
    suspend fun cacheRecipes(recipes: List<Recipe>)
    fun getCachedRecipes(): Flow<List<Recipe>>

    sealed interface Result<out T> {
        data class Success<out T>(val data: T) : Result<T>
        data class Error(val exception: Throwable, val message: String) : Result<Nothing>
    }
}
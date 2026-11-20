package com.example.whattoeat.domain.usecase

import com.example.whattoeat.domain.model.Recipe
import com.example.whattoeat.domain.model.UserPreferences
import com.example.whattoeat.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(preferences: UserPreferences) = repository.searchRecipes(preferences)
}

@Singleton
class GetRecipeByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: String) = repository.getRecipeById(id)
}

@Singleton
class GetRandomRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(count: Int = 5) = repository.getRandomRecipes(count)
}

@Singleton
class GetCategoriesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke() = repository.getCategories()
}

@Singleton
class GetAreasUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke() = repository.getAreas()
}

@Singleton
class GetIngredientsUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke() = repository.getIngredients()
}

// Favorites
@Singleton
class AddToFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(recipe: Recipe) = repository.addToFavorites(recipe)
}

@Singleton
class RemoveFromFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(recipeId: String) = repository.removeFromFavorites(recipeId)
}

@Singleton
class GetFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> = repository.getFavorites()
}

@Singleton
class IsFavoriteUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(recipeId: String) = repository.isFavorite(recipeId)
}

// History
@Singleton
class AddToHistoryUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(recipe: Recipe) = repository.addToHistory(recipe)
}

@Singleton
class GetHistoryUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> = repository.getHistory()
}

@Singleton
class ClearHistoryUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke() = repository.clearHistory()
}

// Cache
@Singleton
class GetCachedRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> = repository.getCachedRecipes()
}
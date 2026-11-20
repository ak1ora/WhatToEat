package com.example.whattoeat.data.repository

import com.example.whattoeat.data.api.TheMealDbApi
import com.example.whattoeat.data.db.AppDatabase
import com.example.whattoeat.data.db.FavoriteEntity
import com.example.whattoeat.data.db.HistoryEntity
import com.example.whattoeat.data.db.RecipeCacheEntity
import com.example.whattoeat.data.mapper.RecipeMapper
import com.example.whattoeat.domain.model.Difficulty
import com.example.whattoeat.domain.model.Recipe
import com.example.whattoeat.domain.model.UserPreferences
import com.example.whattoeat.domain.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val api: TheMealDbApi,
    private val database: AppDatabase,
    private val favoriteDao: com.example.whattoeat.data.db.FavoriteDao,
    private val historyDao: com.example.whattoeat.data.db.HistoryDao,
    private val cacheDao: com.example.whattoeat.data.db.RecipeCacheDao
) : RecipeRepository {

    override suspend fun searchRecipes(preferences: UserPreferences): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            var meals = api.getRandomMeal().meals?.map { RecipeMapper.toDomainSummary(it) } ?: emptyList()

            // Apply filters based on preferences
            meals = meals.filter { recipe ->
                // Time filter
                preferences.maxTimeMinutes?.let { maxTime ->
                    RecipeMapper.estimateTimeMinutes(recipe.ingredients.size, recipe.instructionSteps.size) <= maxTime
                } ?: true
            }.filter { recipe ->
                // Difficulty filter
                preferences.difficulty?.let { difficulty ->
                    RecipeMapper.estimateDifficulty(recipe.ingredients.size, recipe.instructionSteps.size) == difficulty
                } ?: true
            }.filter { recipe ->
                // Cuisine/Area filter
                preferences.cuisine?.let { cuisine ->
                    recipe.area?.equals(cuisine, ignoreCase = true) == true
                } ?: true
            }.filter { recipe ->
                // Excluded ingredients
                preferences.excludedIngredients.none { excluded ->
                    recipe.ingredients.any { it.name.contains(excluded, ignoreCase = true) }
                }
            }.filter { recipe ->
                // Diet filter
                preferences.diet?.let { diet ->
                    matchesDiet(recipe, diet)
                } ?: true
            }

            // If we have available ingredients, try to find recipes with those ingredients
            if (preferences.availableIngredients.isNotEmpty()) {
                val ingredientMeals = preferences.availableIngredients.flatMap { ingredient ->
                    try {
                        api.filterByIngredient(ingredient).meals?.map { RecipeMapper.toDomainSummary(it) } ?: emptyList()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }.distinctBy { it.id }

                if (ingredientMeals.isNotEmpty()) {
                    // Merge with existing, prioritizing ones with matching ingredients
                    meals = (ingredientMeals + meals).distinctBy { it.id }
                }
            }

            // Get full details for top results
            val detailedMeals = meals.take(10).map { summary ->
                try {
                    api.getMealById(summary.id).meals?.firstOrNull()?.let { RecipeMapper.toDomain(it) } ?: summary
                } catch (e: Exception) {
                    summary
                }
            }

            // Score and sort
            val scoredMeals = detailedMeals.map { recipe ->
                recipe to scoreRecipe(recipe, preferences)
            }.sortedByDescending { it.second }
                .map { it.first }

            // Cache results
            cacheRecipes(scoredMeals)

            Result.Success(scoredMeals)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Unknown error")
        }
    }

    private fun scoreRecipe(recipe: Recipe, preferences: UserPreferences): Int {
        var score = 0

        // Time match
        preferences.maxTimeMinutes?.let { maxTime ->
            val recipeTime = RecipeMapper.estimateTimeMinutes(recipe.ingredients.size, recipe.instructionSteps.size)
            if (recipeTime <= maxTime) score += 30
            else score -= 20
        }

        // Difficulty match
        preferences.difficulty?.let { difficulty ->
            val recipeDifficulty = RecipeMapper.estimateDifficulty(recipe.ingredients.size, recipe.instructionSteps.size)
            if (recipeDifficulty == difficulty) score += 25
        }

        // Cuisine match
        preferences.cuisine?.let { cuisine ->
            if (recipe.area?.equals(cuisine, ignoreCase = true) == true) score += 20
        }

        // Available ingredients overlap
        if (preferences.availableIngredients.isNotEmpty()) {
            val overlap = recipe.ingredients.count { ing ->
                preferences.availableIngredients.any { it.contains(ing.name, ignoreCase = true) }
            }
            score += overlap * 5
        }

        // Excluded ingredients penalty
        val excludedCount = recipe.ingredients.count { ing ->
            preferences.excludedIngredients.any { it.contains(ing.name, ignoreCase = true) }
        }
        score -= excludedCount * 20

        // Diet match
        preferences.diet?.let { diet ->
            if (matchesDiet(recipe, diet)) score += 15
        }

        // Meal type match (based on category/tags)
        preferences.mealType?.let { mealType ->
            if (matchesMealType(recipe, mealType)) score += 10
        }

        return score
    }

    private fun matchesDiet(recipe: Recipe, diet: com.example.whattoeat.domain.model.Diet): Boolean {
        val ingredientNames = recipe.ingredients.map { it.name.lowercase() }
        val allText = (recipe.title + " " + recipe.instructions + " " + ingredientNames.joinToString(" ")).lowercase()

        return when (diet) {
            com.example.whattoeat.domain.model.Diet.Vegetarian -> !hasMeat(ingredientNames)
            com.example.whattoeat.domain.model.Diet.Vegan -> !hasAnimalProducts(ingredientNames)
            com.example.whattoeat.domain.model.Diet.GlutenFree -> !hasGluten(ingredientNames)
            com.example.whattoeat.domain.model.Diet.DairyFree -> !hasDairy(ingredientNames)
            com.example.whattoeat.domain.model.Diet.Keto -> isKetoFriendly(ingredientNames)
            com.example.whattoeat.domain.model.Diet.LowCarb -> isLowCarb(ingredientNames)
            com.example.whattoeat.domain.model.Diet.HighProtein -> isHighProtein(ingredientNames)
            else -> true
        }
    }

    private fun hasMeat(ingredients: List<String>): Boolean {
        val meats = listOf("chicken", "beef", "pork", "lamb", "turkey", "duck", "veal", "bacon", "ham", "sausage", "meat", "курица", "говядина", "свинина", "баранина", "индейка", "утка", "телятина", "бекон", "ветчина", "сосиски", "мясо")
        return ingredients.any { ing -> meats.any { it in ing } }
    }

    private fun hasAnimalProducts(ingredients: List<String>): Boolean {
        return hasMeat(ingredients) || hasDairy(ingredients) || hasEggs(ingredients)
    }

    private fun hasDairy(ingredients: List<String>): Boolean {
        val dairy = listOf("milk", "cheese", "butter", "cream", "yogurt", "cottage cheese", "sour cream", "moloko", "сыр", "масло", "сметана", "творог", "йогурт", "молоко", "сливки", "кефир", "ряженка")
        return ingredients.any { ing -> dairy.any { it in ing } }
    }

    private fun hasEggs(ingredients: List<String>): Boolean {
        val eggs = listOf("egg", "яйцо", "яйца", "яйцу")
        return ingredients.any { ing -> eggs.any { it in ing } }
    }

    private fun hasGluten(ingredients: List<String>): Boolean {
        val gluten = listOf("wheat", "flour", "bread", "pasta", "noodles", "soy sauce", "pшница", "мука", "хлеб", "паста", "лапша", "соевый соус", "булгур", "кускус", "манка", "пшениц")
        return ingredients.any { ing -> gluten.any { it in ing } }
    }

    private fun isKetoFriendly(ingredients: List<String>): Boolean {
        val highCarb = listOf("rice", "potato", "pasta", "bread", "sugar", "honey", "flour", "corn", "beans", "lentils", "рис", "картофель", "паста", "хлеб", "сахар", "мёд", "мука", "кукуруза", "фасоль", "чечевица", "гречка", "овсянка")
        val highCarbCount = ingredients.count { ing -> highCarb.any { it in ing } }
        return highCarbCount <= 1
    }

    private fun isLowCarb(ingredients: List<String>): Boolean {
        val highCarb = listOf("rice", "potato", "pasta", "bread", "sugar", "flour", "corn", "рис", "картофель", "паста", "хлеб", "сахар", "мука", "кукуруза", "гречка", "овсянка")
        val highCarbCount = ingredients.count { ing -> highCarb.any { it in ing } }
        return highCarbCount <= 2
    }

    private fun isHighProtein(ingredients: List<String>): Boolean {
        val protein = listOf("chicken", "beef", "fish", "egg", "tofu", "tempeh", "cottage cheese", "greek yogurt", "protein", "курица", "говядина", "рыба", "яйцо", "тофу", "творог", "греческий йогурт", "протеин", "тунец", "лосось", "минтай", "сельдь")
        return ingredients.any { ing -> protein.any { it in ing } }
    }

    private fun matchesMealType(recipe: Recipe, mealType: com.example.whattoeat.domain.model.MealType): Boolean {
        val text = (recipe.title + " " + recipe.category.orEmpty() + " " + recipe.tags.joinToString(" ")).lowercase()
        return when (mealType) {
            com.example.whattoeat.domain.model.MealType.Breakfast -> text.contains("breakfast") || text.contains("завтрак") || text.contains("porridge") || text.contains("eggs") || text.contains("pancake") || text.contains("каша") || text.contains("яичн") || text.contains("блин") || text.contains("сырник") || text.contains("омлет")
            com.example.whattoeat.domain.model.MealType.Lunch -> text.contains("lunch") || text.contains("обед") || text.contains("soup") || text.contains("суп") || text.contains("main course") || text.contains("основное") || text.contains("pasta") || text.contains("паста") || text.contains("risotto") || text.contains("ризотто")
            com.example.whattoeat.domain.model.MealType.Dinner -> text.contains("dinner") || text.contains("ужин") || text.contains("salad") || text.contains("салат") || text.contains("grill") || text.contains("grilled") || text.contains("запекан") || text.contains("stew") || text.contains("рагу") || text.contains("жаркое")
            com.example.whattoeat.domain.model.MealType.Snack -> text.contains("snack") || text.contains("перекус") || text.contains("appetizer") || text.contains("закуска") || text.contains("sandwich") || text.contains("бутерброд") || text.contains("toast") || text.contains("тост") || text.contains("smoothie") || text.contains("смузи")
        }
    }

    override suspend fun getRecipeById(id: String): Result<Recipe> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMealById(id)
            response.meals?.firstOrNull()?.let { RecipeMapper.toDomain(it) }
                ?.let { Result.Success(it) }
                ?: Result.Error(IllegalArgumentException("Recipe not found"), "Recipe not found")
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Unknown error")
        }
    }

    override suspend fun getRandomRecipes(count: Int): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            val meals = mutableListOf<Recipe>()
            repeat(count) {
                try {
                    val response = api.getRandomMeal()
                    response.meals?.firstOrNull()?.let { meals.add(RecipeMapper.toDomain(it)) }
                } catch (e: Exception) {
                    // Ignore individual failures
                }
            }
            Result.Success(meals)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Unknown error")
        }
    }

    override suspend fun getCategories(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCategories("list")
            val categories = response.meals?.map { it.strCategory }?.sorted() ?: emptyList()
            Result.Success(categories)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Unknown error")
        }
    }

    override suspend fun getAreas(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAreas("list")
            val areas = response.meals?.map { it.strArea }?.sorted() ?: emptyList()
            Result.Success(areas)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Unknown error")
        }
    }

    override suspend fun getIngredients(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getIngredients("list")
            val ingredients = response.meals?.map { it.strIngredient }?.sorted() ?: emptyList()
            Result.Success(ingredients)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Unknown error")
        }
    }

    // Favorites
    override suspend fun addToFavorites(recipe: Recipe) = withContext(Dispatchers.IO) {
        val entity = FavoriteEntity(
            id = recipe.id,
            title = recipe.title,
            imageUrl = recipe.imageUrl,
            category = recipe.category,
            area = recipe.area,
            instructions = recipe.instructions,
            ingredientsJson = com.example.whattoeat.data.db.Converters().fromIngredientList(recipe.ingredients),
            tagsJson = com.example.whattoeat.data.db.Converters().fromStringList(recipe.tags),
            youtubeUrl = recipe.youtubeUrl,
            sourceUrl = recipe.sourceUrl,
            dateModified = recipe.dateModified
        )
        favoriteDao.insert(entity)
    }

    override suspend fun removeFromFavorites(recipeId: String) = withContext(Dispatchers.IO) {
        favoriteDao.deleteById(recipeId)
    }

    override fun getFavorites(): Flow<List<Recipe>> = favoriteDao.getAll().map { entities ->
        entities.map { entity ->
            Recipe(
                id = entity.id,
                title = entity.title,
                imageUrl = entity.imageUrl,
                category = entity.category,
                area = entity.area,
                instructions = entity.instructions,
                ingredients = com.example.whattoeat.data.db.Converters().fromIngredientList(entity.ingredientsJson),
                tags = com.example.whattoeat.data.db.Converters().fromStringList(entity.tagsJson),
                youtubeUrl = entity.youtubeUrl,
                sourceUrl = entity.sourceUrl,
                dateModified = entity.dateModified
            )
        }
    }

    override suspend fun isFavorite(recipeId: String): Boolean = withContext(Dispatchers.IO) {
        favoriteDao.isFavorite(recipeId)
    }

    // History
    override suspend fun addToHistory(recipe: Recipe) = withContext(Dispatchers.IO) {
        val entity = HistoryEntity(
            recipeId = recipe.id,
            title = recipe.title,
            imageUrl = recipe.imageUrl,
            category = recipe.category,
            area = recipe.area,
            instructions = recipe.instructions,
            ingredientsJson = com.example.whattoeat.data.db.Converters().fromIngredientList(recipe.ingredients),
            tagsJson = com.example.whattoeat.data.db.Converters().fromStringList(recipe.tags),
            youtubeUrl = recipe.youtubeUrl,
            sourceUrl = recipe.sourceUrl,
            dateModified = recipe.dateModified
        )
        historyDao.insert(entity)
    }

    override fun getHistory(): Flow<List<Recipe>> = historyDao.getRecent().map { entities ->
        entities.map { entity ->
            Recipe(
                id = entity.recipeId,
                title = entity.title,
                imageUrl = entity.imageUrl,
                category = entity.category,
                area = entity.area,
                instructions = entity.instructions,
                ingredients = com.example.whattoeat.data.db.Converters().fromIngredientList(entity.ingredientsJson),
                tags = com.example.whattoeat.data.db.Converters().fromStringList(entity.tagsJson),
                youtubeUrl = entity.youtubeUrl,
                sourceUrl = entity.sourceUrl,
                dateModified = entity.dateModified
            )
        }
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        historyDao.clearAll()
    }

    // Cache
    override suspend fun cacheRecipes(recipes: List<Recipe>) = withContext(Dispatchers.IO) {
        val entities = recipes.map { recipe ->
            RecipeCacheEntity(
                id = recipe.id,
                title = recipe.title,
                imageUrl = recipe.imageUrl,
                category = recipe.category,
                area = recipe.area,
                instructions = recipe.instructions,
                ingredientsJson = com.example.whattoeat.data.db.Converters().fromIngredientList(recipe.ingredients),
                tagsJson = com.example.whattoeat.data.db.Converters().fromStringList(recipe.tags),
                youtubeUrl = recipe.youtubeUrl,
                sourceUrl = recipe.sourceUrl,
                dateModified = recipe.dateModified
            )
        }
        cacheDao.insertAll(entities)
    }

    override fun getCachedRecipes(): Flow<List<Recipe>> = cacheDao.getRecent().map { entities ->
        entities.map { entity ->
            Recipe(
                id = entity.id,
                title = entity.title,
                imageUrl = entity.imageUrl,
                category = entity.category,
                area = entity.area,
                instructions = entity.instructions,
                ingredients = com.example.whattoeat.data.db.Converters().fromIngredientList(entity.ingredientsJson),
                tags = com.example.whattoeat.data.db.Converters().fromStringList(entity.tagsJson),
                youtubeUrl = entity.youtubeUrl,
                sourceUrl = entity.sourceUrl,
                dateModified = entity.dateModified
            )
        }
    }
}
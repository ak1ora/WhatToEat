package com.example.whattoeat.data.db

import androidx.room.TypeConverter
import com.example.whattoeat.domain.model.Difficulty
import com.example.whattoeat.domain.model.Diet
import com.example.whattoeat.domain.model.Ingredient
import com.example.whattoeat.domain.model.MealType
import com.example.whattoeat.domain.model.Nutrients
import com.example.whattoeat.domain.model.Recipe
import com.example.whattoeat.domain.model.UserPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

class Converters {

    @TypeConverter
    fun fromIngredientList(value: String?): List<Ingredient> {
        return value?.let { json.decodeFromString<List<Ingredient>>(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromIngredientList(list: List<Ingredient>?): String {
        return json.encodeToString(list ?: emptyList())
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.let { json.decodeFromString<List<String>>(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return json.encodeToString(list ?: emptyList())
    }

    @TypeConverter
    fun fromMealType(value: String?): MealType? {
        return value?.let { MealType.valueOf(it) }
    }

    @TypeConverter
    fun fromMealType(mealType: MealType?): String? {
        return mealType?.name
    }

    @TypeConverter
    fun fromDifficulty(value: String?): Difficulty? {
        return value?.let { Difficulty.valueOf(it) }
    }

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty?): String? {
        return difficulty?.name
    }

    @TypeConverter
    fun fromDiet(value: String?): Diet? {
        return value?.let { Diet.valueOf(it) }
    }

    @TypeConverter
    fun fromDiet(diet: Diet?): String? {
        return diet?.name
    }

    @TypeConverter
    fun fromNutrients(value: String?): Nutrients? {
        return value?.let { json.decodeFromString<Nutrients>(it) }
    }

    @TypeConverter
    fun fromNutrients(nutrients: Nutrients?): String? {
        return nutrients?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun fromUserPreferences(value: String?): UserPreferences? {
        return value?.let { json.decodeFromString<UserPreferences>(it) }
    }

    @TypeConverter
    fun fromUserPreferences(prefs: UserPreferences?): String? {
        return prefs?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun fromRecipe(value: String?): Recipe? {
        return value?.let { json.decodeFromString<Recipe>(it) }
    }

    @TypeConverter
    fun fromRecipe(recipe: Recipe?): String? {
        return recipe?.let { json.encodeToString(it) }
    }
}
package com.example.whattoeat.data.api

import com.example.whattoeat.data.api.dto.MealDbResponse
import com.example.whattoeat.data.api.dto.MealDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMealDbApi {

    // Search by name
    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): MealDbResponse

    // Filter by main ingredient
    @GET("filter.php")
    suspend fun filterByIngredient(@Query("i") ingredient: String): MealDbResponse

    // Filter by category
    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): MealDbResponse

    // Filter by area (cuisine)
    @GET("filter.php")
    suspend fun filterByArea(@Query("a") area: String): MealDbResponse

    // Get random meal
    @GET("random.php")
    suspend fun getRandomMeal(): MealDbResponse

    // Get meal by ID
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealDetailResponse

    // List all categories
    @GET("list.php")
    suspend fun getCategories(@Query("c") list: String): MealDbResponse

    // List all areas
    @GET("list.php")
    suspend fun getAreas(@Query("a") list: String): MealDbResponse

    // List all ingredients
    @GET("list.php")
    suspend fun getIngredients(@Query("i") list: String): MealDbResponse
}
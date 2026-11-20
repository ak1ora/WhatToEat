package com.example.whattoeat.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "favorites")
@Serializable
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val category: String?,
    val area: String?,
    val instructions: String,
    val ingredientsJson: String,
    val tagsJson: String,
    val youtubeUrl: String?,
    val sourceUrl: String?,
    val dateModified: String?,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
@Serializable
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: String,
    val title: String,
    val imageUrl: String,
    val category: String?,
    val area: String?,
    val instructions: String,
    val ingredientsJson: String,
    val tagsJson: String,
    val youtubeUrl: String?,
    val sourceUrl: String?,
    val dateModified: String?,
    val viewedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recipe_cache")
@Serializable
data class RecipeCacheEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val category: String?,
    val area: String?,
    val instructions: String,
    val ingredientsJson: String,
    val tagsJson: String,
    val youtubeUrl: String?,
    val sourceUrl: String?,
    val dateModified: String?,
    val cachedTimestamp: Long = System.currentTimeMillis()
)
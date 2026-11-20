package com.example.whattoeat.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Delete
    suspend fun delete(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :recipeId")
    suspend fun deleteById(recipeId: String)

    @Query("SELECT * FROM favorites ORDER BY addedTimestamp DESC")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :recipeId)")
    suspend fun isFavorite(recipeId: String): Boolean

    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getCount(): Int
}

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity)

    @Query("DELETE FROM history WHERE recipeId = :recipeId")
    suspend fun deleteByRecipeId(recipeId: String)

    @Query("DELETE FROM history")
    suspend fun clearAll()

    @Query("SELECT * FROM history ORDER BY viewedTimestamp DESC LIMIT 50")
    fun getRecent(): Flow<List<HistoryEntity>>

    @Query("SELECT COUNT(*) FROM history")
    suspend fun getCount(): Int
}

@Dao
interface RecipeCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeCacheEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeCacheEntity)

    @Query("DELETE FROM recipe_cache")
    suspend fun clearAll()

    @Query("SELECT * FROM recipe_cache ORDER BY cachedTimestamp DESC LIMIT 50")
    fun getRecent(): Flow<List<RecipeCacheEntity>>

    @Query("SELECT COUNT(*) FROM recipe_cache")
    suspend fun getCount(): Int
}
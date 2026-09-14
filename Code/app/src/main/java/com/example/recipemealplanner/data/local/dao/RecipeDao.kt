package com.example.recipemealplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.recipemealplanner.data.local.dto.RecipeWithIngredients
import com.example.recipemealplanner.data.local.entity.Recipe
import com.example.recipemealplanner.data.local.entity.RecipeIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Int): Flow<RecipeWithIngredients?>

    @Transaction
    @Query(
        """
        SELECT DISTINCT r.* FROM recipes r
        LEFT JOIN recipe_ingredients ri ON ri.recipeId = r.id
        WHERE (:category IS NULL OR r.category = :category)
        AND (:query = '' OR r.name LIKE '%' || :query || '%' OR ri.name LIKE '%' || :query || '%')
        ORDER BY r.name ASC
        """
    )
    fun searchRecipes(query: String, category: String?): Flow<List<RecipeWithIngredients>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE lastViewedAt IS NOT NULL ORDER BY lastViewedAt DESC LIMIT 10")
    fun getRecentlyViewedRecipes(): Flow<List<Recipe>>

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Insert
    suspend fun insertIngredients(ingredients: List<RecipeIngredient>)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsForRecipe(recipeId: Int)

    @Query("UPDATE recipes SET lastViewedAt = :timestamp WHERE id = :recipeId")
    suspend fun markViewed(recipeId: Int, timestamp: Long)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun setFavorite(recipeId: Int, isFavorite: Boolean)
}

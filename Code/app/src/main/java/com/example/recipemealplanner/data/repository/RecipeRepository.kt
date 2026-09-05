package com.example.recipemealplanner.data.repository

import com.example.recipemealplanner.data.local.dao.RecipeDao
import com.example.recipemealplanner.data.local.dto.RecipeWithIngredients
import com.example.recipemealplanner.data.local.entity.Recipe
import com.example.recipemealplanner.data.local.entity.RecipeIngredient
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {

    fun getAllRecipes(): Flow<List<RecipeWithIngredients>> = recipeDao.getAllRecipes()

    fun getRecipeById(id: Int): Flow<RecipeWithIngredients?> = recipeDao.getRecipeById(id)

    fun searchRecipes(query: String, category: String?): Flow<List<RecipeWithIngredients>> =
        recipeDao.searchRecipes(query, category)

    fun getFavoriteRecipes(): Flow<List<Recipe>> = recipeDao.getFavoriteRecipes()

    fun getRecentlyViewedRecipes(): Flow<List<Recipe>> = recipeDao.getRecentlyViewedRecipes()

    suspend fun saveRecipe(recipe: Recipe, ingredients: List<RecipeIngredient>): Int {
        val recipeId = if (recipe.id == 0) {
            recipeDao.insertRecipe(recipe).toInt()
        } else {
            recipeDao.updateRecipe(recipe)
            recipeDao.deleteIngredientsForRecipe(recipe.id)
            recipe.id
        }
        recipeDao.insertIngredients(ingredients.map { it.copy(recipeId = recipeId) })
        return recipeId
    }

    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)

    suspend fun markViewed(recipeId: Int) = recipeDao.markViewed(recipeId, System.currentTimeMillis())

    suspend fun setFavorite(recipeId: Int, isFavorite: Boolean) = recipeDao.setFavorite(recipeId, isFavorite)
}

package com.example.recipemealplanner.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recipemealplanner.data.local.dto.RecipeWithIngredients
import com.example.recipemealplanner.data.local.entity.Recipe
import com.example.recipemealplanner.data.local.entity.RecipeIngredient
import com.example.recipemealplanner.data.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null)

    val recipes: StateFlow<List<RecipeWithIngredients>> =
        combine(searchQuery, selectedCategory) { query, category -> query to category }
            .flatMapLatest { (query, category) -> repository.searchRecipes(query, category) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRecipes: StateFlow<List<Recipe>> = repository.getFavoriteRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyViewedRecipes: StateFlow<List<Recipe>> = repository.getRecentlyViewedRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getRecipeById(id: Int): Flow<RecipeWithIngredients?> = repository.getRecipeById(id)

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateCategoryFilter(category: String?) {
        selectedCategory.value = category
    }

    fun saveRecipe(recipe: Recipe, ingredients: List<RecipeIngredient>) = viewModelScope.launch {
        repository.saveRecipe(recipe, ingredients)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.deleteRecipe(recipe)
    }

    fun markViewed(recipeId: Int) = viewModelScope.launch {
        repository.markViewed(recipeId)
    }

    fun toggleFavorite(recipe: Recipe) = viewModelScope.launch {
        repository.setFavorite(recipe.id, !recipe.isFavorite)
    }

    class Factory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeViewModel(repository) as T
        }
    }
}

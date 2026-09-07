// Assignment by Thai Anh Quan

package com.example.recipemealplanner.ui.mealplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recipemealplanner.data.local.dto.MealPlanWithRecipe
import com.example.recipemealplanner.data.local.entity.Recipe
import com.example.recipemealplanner.data.repository.MealPlanRepository
import com.example.recipemealplanner.data.repository.RecipeRepository
import com.example.recipemealplanner.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MealPlannerViewModel(
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    val weekDates: List<String> = DateUtils.currentWeekDates()

    val mealPlans: StateFlow<List<MealPlanWithRecipe>> =
        mealPlanRepository.getMealPlansInRange(weekDates.first(), weekDates.last())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecipes: StateFlow<List<Recipe>> =
        recipeRepository.getAllRecipes()
            .map { list -> list.map { it.recipe } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun assignRecipe(date: String, mealType: String, recipeId: Int) = viewModelScope.launch {
        mealPlanRepository.assignRecipe(date, mealType, recipeId)
    }

    fun clearSlot(date: String, mealType: String) = viewModelScope.launch {
        mealPlanRepository.clearSlot(date, mealType)
    }

    class Factory(
        private val mealPlanRepository: MealPlanRepository,
        private val recipeRepository: RecipeRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MealPlannerViewModel(mealPlanRepository, recipeRepository) as T
        }
    }
}

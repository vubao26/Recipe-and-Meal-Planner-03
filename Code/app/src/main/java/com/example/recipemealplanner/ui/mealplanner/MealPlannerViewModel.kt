package com.example.recipemealplanner.ui.mealplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recipemealplanner.data.local.dto.MealPlanWithRecipe
import com.example.recipemealplanner.data.local.entity.Recipe
import com.example.recipemealplanner.data.repository.MealPlanRepository
import com.example.recipemealplanner.data.repository.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealPlannerViewModel(
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val weekDates: List<String> = buildWeekDates()

    val mealPlans: StateFlow<List<MealPlanWithRecipe>> =
        mealPlanRepository.getMealPlansInRange(weekDates.first(), weekDates.last())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecipes: StateFlow<List<Recipe>> =
        recipeRepository.getAllRecipes()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun assignRecipe(date: String, mealType: String, recipeId: Int) = viewModelScope.launch {
        mealPlanRepository.assignRecipe(date, mealType, recipeId)
    }

    fun clearSlot(date: String, mealType: String) = viewModelScope.launch {
        mealPlanRepository.clearSlot(date, mealType)
    }

    private fun buildWeekDates(): List<String> {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return (0..6).map {
            val date = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            date
        }
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

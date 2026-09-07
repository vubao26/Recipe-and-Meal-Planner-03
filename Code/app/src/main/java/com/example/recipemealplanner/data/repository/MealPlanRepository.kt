// Assignment by Thai Anh Quan

package com.example.recipemealplanner.data.repository

import com.example.recipemealplanner.data.local.dao.MealPlanDao
import com.example.recipemealplanner.data.local.dto.MealPlanWithRecipe
import com.example.recipemealplanner.data.local.entity.MealPlanEntry
import kotlinx.coroutines.flow.Flow

class MealPlanRepository(private val mealPlanDao: MealPlanDao) {

    fun getMealPlansInRange(startDate: String, endDate: String): Flow<List<MealPlanWithRecipe>> =
        mealPlanDao.getMealPlansInRange(startDate, endDate)

    suspend fun assignRecipe(date: String, mealType: String, recipeId: Int) {
        mealPlanDao.insertMealPlan(MealPlanEntry(date = date, mealType = mealType, recipeId = recipeId))
    }

    suspend fun clearSlot(date: String, mealType: String) {
        mealPlanDao.clearSlot(date, mealType)
    }
}

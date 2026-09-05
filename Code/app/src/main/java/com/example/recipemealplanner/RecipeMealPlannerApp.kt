package com.example.recipemealplanner

import android.app.Application
import com.example.recipemealplanner.data.local.AppDatabase
import com.example.recipemealplanner.data.repository.MealPlanRepository
import com.example.recipemealplanner.data.repository.RecipeRepository

class RecipeMealPlannerApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val recipeRepository by lazy { RecipeRepository(database.recipeDao()) }
    val mealPlanRepository by lazy { MealPlanRepository(database.mealPlanDao()) }
}

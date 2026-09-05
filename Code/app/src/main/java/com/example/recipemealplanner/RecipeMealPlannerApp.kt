package com.example.recipemealplanner

import android.app.Application
import com.example.recipemealplanner.data.local.AppDatabase
import com.example.recipemealplanner.data.repository.MealPlanRepository
import com.example.recipemealplanner.data.repository.RecipeRepository
import com.example.recipemealplanner.data.repository.ShoppingRepository

class RecipeMealPlannerApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val recipeRepository by lazy { RecipeRepository(database.recipeDao()) }
    val mealPlanRepository by lazy { MealPlanRepository(database.mealPlanDao()) }
    val shoppingRepository by lazy { ShoppingRepository(database.shoppingDao()) }
}

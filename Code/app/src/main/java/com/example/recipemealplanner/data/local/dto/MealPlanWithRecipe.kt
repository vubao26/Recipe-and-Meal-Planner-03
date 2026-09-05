package com.example.recipemealplanner.data.local.dto

data class MealPlanWithRecipe(
    val id: Int,
    val date: String,
    val mealType: String,
    val recipeId: Int,
    val recipeName: String
)

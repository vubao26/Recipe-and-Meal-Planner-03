package com.example.recipemealplanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipemealplanner.data.local.dao.MealPlanDao
import com.example.recipemealplanner.data.local.dao.RecipeDao
import com.example.recipemealplanner.data.local.dao.ShoppingDao
import com.example.recipemealplanner.data.local.entity.MealPlanEntry
import com.example.recipemealplanner.data.local.entity.PantryItem
import com.example.recipemealplanner.data.local.entity.Recipe
import com.example.recipemealplanner.data.local.entity.RecipeIngredient

@Database(
    entities = [Recipe::class, MealPlanEntry::class, RecipeIngredient::class, PantryItem::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun shoppingDao(): ShoppingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_meal_planner_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

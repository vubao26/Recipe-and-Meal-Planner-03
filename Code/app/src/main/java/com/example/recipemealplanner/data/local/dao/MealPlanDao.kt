package com.example.recipemealplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipemealplanner.data.local.dto.MealPlanWithRecipe
import com.example.recipemealplanner.data.local.entity.MealPlanEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {

    @Query(
        """
        SELECT m.id AS id, m.date AS date, m.mealType AS mealType,
               m.recipeId AS recipeId, r.name AS recipeName
        FROM meal_plan_entries AS m
        INNER JOIN recipes AS r ON m.recipeId = r.id
        WHERE m.date BETWEEN :startDate AND :endDate
        ORDER BY m.date ASC, m.mealType ASC
        """
    )
    fun getMealPlansInRange(startDate: String, endDate: String): Flow<List<MealPlanWithRecipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(entry: MealPlanEntry): Long

    @Delete
    suspend fun deleteMealPlan(entry: MealPlanEntry)

    @Query("DELETE FROM meal_plan_entries WHERE date = :date AND mealType = :mealType")
    suspend fun clearSlot(date: String, mealType: String)
}

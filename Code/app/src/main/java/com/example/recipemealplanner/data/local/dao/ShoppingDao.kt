package com.example.recipemealplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipemealplanner.data.local.dto.AggregatedIngredient
import com.example.recipemealplanner.data.local.entity.PantryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {

    @Query(
        """
        SELECT ri.name AS name, ri.unit AS unit, SUM(ri.quantity) AS totalQuantity
        FROM meal_plan_entries m
        INNER JOIN recipe_ingredients ri ON ri.recipeId = m.recipeId
        WHERE m.date BETWEEN :startDate AND :endDate
        GROUP BY LOWER(TRIM(ri.name)), ri.unit
        ORDER BY ri.name ASC
        """
    )
    fun getAggregatedIngredients(startDate: String, endDate: String): Flow<List<AggregatedIngredient>>

    @Query("SELECT * FROM pantry_items")
    fun getAllPantryItems(): Flow<List<PantryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPantryItem(item: PantryItem)

    @Query("DELETE FROM pantry_items WHERE normalizedName = :normalizedName")
    suspend fun removePantryItem(normalizedName: String)
}

// Assignment by Thai Anh Quan

package com.example.recipemealplanner.data.repository

import com.example.recipemealplanner.data.local.dao.ShoppingDao
import com.example.recipemealplanner.data.local.dto.AggregatedIngredient
import com.example.recipemealplanner.data.local.entity.PantryItem
import kotlinx.coroutines.flow.Flow

class ShoppingRepository(private val shoppingDao: ShoppingDao) {

    fun getAggregatedIngredients(startDate: String, endDate: String): Flow<List<AggregatedIngredient>> =
        shoppingDao.getAggregatedIngredients(startDate, endDate)

    fun getPantryItems(): Flow<List<PantryItem>> = shoppingDao.getAllPantryItems()

    suspend fun togglePantryItem(name: String, currentlyInPantry: Boolean) {
        val normalized = name.trim().lowercase()
        if (currentlyInPantry) {
            shoppingDao.removePantryItem(normalized)
        } else {
            shoppingDao.addPantryItem(PantryItem(normalizedName = normalized, displayName = name.trim()))
        }
    }
}

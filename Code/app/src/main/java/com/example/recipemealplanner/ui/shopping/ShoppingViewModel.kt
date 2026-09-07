// Assignment by Thai Anh Quan

package com.example.recipemealplanner.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recipemealplanner.data.repository.ShoppingRepository
import com.example.recipemealplanner.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ShoppingListItem(
    val name: String,
    val unit: String,
    val quantity: Double,
    val inPantry: Boolean
)

class ShoppingViewModel(private val repository: ShoppingRepository) : ViewModel() {

    private val weekDates = DateUtils.currentWeekDates()

    val shoppingItems: StateFlow<List<ShoppingListItem>> = combine(
        repository.getAggregatedIngredients(weekDates.first(), weekDates.last()),
        repository.getPantryItems()
    ) { aggregated, pantry ->
        val pantryNames = pantry.map { it.normalizedName }.toSet()
        aggregated.map { ing ->
            ShoppingListItem(
                name = ing.name,
                unit = ing.unit,
                quantity = ing.totalQuantity,
                inPantry = pantryNames.contains(ing.name.trim().lowercase())
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun togglePantry(item: ShoppingListItem) = viewModelScope.launch {
        repository.togglePantryItem(item.name, item.inPantry)
    }

    class Factory(private val repository: ShoppingRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShoppingViewModel(repository) as T
        }
    }
}

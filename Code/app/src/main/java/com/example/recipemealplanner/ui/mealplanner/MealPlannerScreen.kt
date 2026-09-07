// Assignment by Thai Anh Quan

package com.example.recipemealplanner.ui.mealplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipemealplanner.data.local.dto.MealPlanWithRecipe
import com.example.recipemealplanner.data.local.entity.Recipe
import java.text.SimpleDateFormat
import java.util.Locale

private val mealTypes = listOf("Breakfast", "Lunch", "Dinner")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(viewModel: MealPlannerViewModel) {
    val mealPlans by viewModel.mealPlans.collectAsState()
    val allRecipes by viewModel.allRecipes.collectAsState()
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var pickerMealType by remember { mutableStateOf<String?>(null) }

    val selectedDate = viewModel.weekDates[selectedDayIndex]
    val displayFormat = remember { SimpleDateFormat("EEE d MMM", Locale.US) }
    val parseFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Meal Planner") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(selectedTabIndex = selectedDayIndex) {
                viewModel.weekDates.forEachIndexed { index, date ->
                    Tab(
                        selected = index == selectedDayIndex,
                        onClick = { selectedDayIndex = index },
                        text = { Text(displayFormat.format(parseFormat.parse(date)!!)) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mealTypes) { mealType ->
                    val entry = mealPlans.find { it.date == selectedDate && it.mealType == mealType }
                    MealSlotCard(
                        mealType = mealType,
                        entry = entry,
                        onClick = { if (entry == null) pickerMealType = mealType },
                        onRemove = { viewModel.clearSlot(selectedDate, mealType) }
                    )
                }
            }
        }
    }

    pickerMealType?.let { mealType ->
        RecipePickerDialog(
            recipes = allRecipes,
            onDismiss = { pickerMealType = null },
            onSelect = { recipe ->
                viewModel.assignRecipe(selectedDate, mealType, recipe.id)
                pickerMealType = null
            }
        )
    }
}

@Composable
private fun MealSlotCard(
    mealType: String,
    entry: MealPlanWithRecipe?,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (entry == null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = mealType, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry?.recipeName ?: "Tap to add a recipe",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (entry != null) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Close, contentDescription = "Remove")
                }
            }
        }
    }
}

@Composable
private fun RecipePickerDialog(
    recipes: List<Recipe>,
    onDismiss: () -> Unit,
    onSelect: (Recipe) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose a recipe") },
        text = {
            if (recipes.isEmpty()) {
                Text("You don't have any recipes yet. Add one from the Recipes tab first.")
            } else {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(recipes, key = { it.id }) { recipe ->
                        Text(
                            text = recipe.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(recipe) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

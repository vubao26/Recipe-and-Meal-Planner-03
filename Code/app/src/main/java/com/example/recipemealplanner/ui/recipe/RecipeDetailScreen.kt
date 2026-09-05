package com.example.recipemealplanner.ui.recipe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    viewModel: RecipeViewModel,
    onEditClick: () -> Unit,
    onDeleteDone: () -> Unit,
    onBack: () -> Unit
) {
    val recipeFlow = remember(recipeId) { viewModel.getRecipeById(recipeId) }
    val item by recipeFlow.collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }
    var desiredServings by remember(item?.recipe?.servings) {
        mutableStateOf(item?.recipe?.servings ?: 1)
    }

    LaunchedEffect(recipeId) {
        viewModel.markViewed(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.recipe?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    item?.recipe?.let { recipe ->
                        IconButton(onClick = { viewModel.toggleFavorite(recipe) }) {
                            Icon(
                                imageVector = if (recipe.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Toggle favorite"
                            )
                        }
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        item?.let { data ->
            val r = data.recipe
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                if (r.imageUri != null) {
                    AsyncImage(
                        model = File(r.imageUri),
                        contentDescription = r.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = r.category, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${r.prepTimeMinutes} min", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Servings", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { if (desiredServings > 1) desiredServings-- }) {
                            Icon(Icons.Filled.Remove, contentDescription = "Decrease servings")
                        }
                        Text(text = "$desiredServings", style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = { desiredServings++ }) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase servings")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Ingredients", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    data.ingredients.forEach { ingredient ->
                        val scaled = scaleQuantity(ingredient.quantity, r.servings, desiredServings)
                        Text(
                            text = "$scaled ${ingredient.unit} ${ingredient.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Instructions", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = r.instructions, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete recipe?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    item?.recipe?.let { viewModel.deleteRecipe(it) }
                    showDeleteDialog = false
                    onDeleteDone()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

private fun scaleQuantity(baseQuantity: Double, baseServings: Int, desiredServings: Int): String {
    if (baseServings <= 0) return formatQuantity(baseQuantity)
    val scaled = baseQuantity * desiredServings / baseServings
    return formatQuantity(scaled)
}

private fun formatQuantity(value: Double): String {
    val rounded = (value * 100).roundToInt() / 100.0
    return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString() else rounded.toString()
}

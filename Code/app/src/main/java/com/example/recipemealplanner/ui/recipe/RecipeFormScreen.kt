package com.example.recipemealplanner.ui.recipe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.recipemealplanner.data.local.entity.Recipe
import com.example.recipemealplanner.data.local.entity.RecipeIngredient
import com.example.recipemealplanner.util.ImageStorage
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

private val categories = listOf("Breakfast", "Lunch", "Dinner", "Dessert", "Snack")

private data class IngredientRow(
    val rowId: String = UUID.randomUUID().toString(),
    val name: String = "",
    val quantity: String = "",
    val unit: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    recipeId: Int?,
    viewModel: RecipeViewModel,
    onSaveDone: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val existing by if (recipeId != null) {
        remember(recipeId) { viewModel.getRecipeById(recipeId) }.collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }
    var instructions by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var ingredientRows by remember { mutableStateOf(listOf(IngredientRow())) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        existing?.let { data ->
            val r = data.recipe
            name = r.name
            category = r.category
            instructions = r.instructions
            prepTime = r.prepTimeMinutes.toString()
            servings = r.servings.toString()
            imagePath = r.imageUri
            isFavorite = r.isFavorite
            if (data.ingredients.isNotEmpty()) {
                ingredientRows = data.ingredients.map {
                    IngredientRow(name = it.name, quantity = formatQty(it.quantity), unit = it.unit)
                }
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val savedPath = ImageStorage.copyToInternalStorage(context, uri)
                if (savedPath != null) imagePath = savedPath
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "Add Recipe" else "Edit Recipe") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imagePath != null) {
                    AsyncImage(
                        model = File(imagePath!!),
                        contentDescription = "Recipe photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
                        Text("Add photo")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = categoryMenuExpanded,
                onExpandedChange = { categoryMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false }
                ) {
                    categories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row {
                OutlinedTextField(
                    value = prepTime,
                    onValueChange = { prepTime = it.filter(Char::isDigit) },
                    label = { Text("Prep time (min)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = servings,
                    onValueChange = { servings = it.filter(Char::isDigit) },
                    label = { Text("Servings") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Ingredients", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            ingredientRows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = row.name,
                        onValueChange = { newVal ->
                            ingredientRows = ingredientRows.toMutableList().also {
                                it[index] = row.copy(name = newVal)
                            }
                        },
                        label = { Text("Ingredient") },
                        modifier = Modifier.weight(2f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = row.quantity,
                        onValueChange = { newVal ->
                            ingredientRows = ingredientRows.toMutableList().also {
                                it[index] = row.copy(quantity = newVal.filter { c -> c.isDigit() || c == '.' })
                            }
                        },
                        label = { Text("Qty") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = row.unit,
                        onValueChange = { newVal ->
                            ingredientRows = ingredientRows.toMutableList().also {
                                it[index] = row.copy(unit = newVal)
                            }
                        },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (ingredientRows.size > 1) {
                            ingredientRows = ingredientRows.toMutableList().also { it.removeAt(index) }
                        }
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Remove ingredient")
                    }
                }
            }
            TextButton(onClick = { ingredientRows = ingredientRows + IngredientRow() }) {
                Text("+ Add ingredient")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val recipe = Recipe(
                        id = recipeId ?: 0,
                        name = name.trim(),
                        category = category,
                        instructions = instructions.trim(),
                        prepTimeMinutes = prepTime.toIntOrNull() ?: 0,
                        servings = servings.toIntOrNull() ?: 1,
                        imageUri = imagePath,
                        isFavorite = isFavorite,
                        lastViewedAt = existing?.recipe?.lastViewedAt
                    )
                    val ingredients = ingredientRows
                        .filter { it.name.isNotBlank() }
                        .map {
                            RecipeIngredient(
                                recipeId = recipeId ?: 0,
                                name = it.name.trim(),
                                quantity = it.quantity.toDoubleOrNull() ?: 0.0,
                                unit = it.unit.trim()
                            )
                        }
                    viewModel.saveRecipe(recipe, ingredients)
                    onSaveDone()
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

private fun formatQty(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

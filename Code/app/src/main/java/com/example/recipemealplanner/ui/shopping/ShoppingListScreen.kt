package com.example.recipemealplanner.ui.shopping

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingViewModel) {
    val items by viewModel.shoppingItems.collectAsState()
    val context = LocalContext.current

    val needToBuy = items.filter { !it.inPantry }
    val alreadyHave = items.filter { it.inPantry }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping List") },
                actions = {
                    IconButton(onClick = {
                        val text = buildShareText(needToBuy)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share shopping list"))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share shopping list")
                    }
                }
            )
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No ingredients needed yet — plan some meals this week first.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    Text(
                        text = "Need to buy (${needToBuy.size})",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                items(needToBuy, key = { "buy_${it.name}_${it.unit}" }) { shoppingItem ->
                    ShoppingRow(shoppingItem, onToggle = { viewModel.togglePantry(shoppingItem) })
                }

                if (alreadyHave.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Already have (${alreadyHave.size})",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(alreadyHave, key = { "have_${it.name}_${it.unit}" }) { shoppingItem ->
                        ShoppingRow(shoppingItem, onToggle = { viewModel.togglePantry(shoppingItem) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ShoppingRow(item: ShoppingListItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.inPantry, onCheckedChange = { onToggle() })
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${formatShoppingQty(item.quantity)} ${item.unit} ${item.name}",
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (item.inPantry) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

private fun buildShareText(items: List<ShoppingListItem>): String {
    if (items.isEmpty()) return "Shopping list is empty — everything is already in the pantry."
    return "Shopping List:\n" + items.joinToString("\n") {
        "- ${formatShoppingQty(it.quantity)} ${it.unit} ${it.name}"
    }
}

private fun formatShoppingQty(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

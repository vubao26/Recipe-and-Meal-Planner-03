package com.example.recipemealplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipemealplanner.RecipeMealPlannerApp
import com.example.recipemealplanner.ui.mealplanner.MealPlannerScreen
import com.example.recipemealplanner.ui.mealplanner.MealPlannerViewModel
import com.example.recipemealplanner.ui.recipe.RecipeDetailScreen
import com.example.recipemealplanner.ui.recipe.RecipeFormScreen
import com.example.recipemealplanner.ui.recipe.RecipeListScreen
import com.example.recipemealplanner.ui.recipe.RecipeViewModel

private sealed class Destination(val route: String, val label: String) {
    data object RecipeList : Destination("recipe_list", "Recipes")
    data object MealPlanner : Destination("meal_planner", "Meal Planner")
}

@Composable
fun AppNavigation(app: RecipeMealPlannerApp) {
    val navController = rememberNavController()
    val recipeViewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModel.Factory(app.recipeRepository)
    )
    val mealPlannerViewModel: MealPlannerViewModel = viewModel(
        factory = MealPlannerViewModel.Factory(app.mealPlanRepository, app.recipeRepository)
    )

    val tabs = listOf(Destination.RecipeList, Destination.MealPlanner)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                tabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (tab is Destination.RecipeList)
                                    Icons.Filled.RestaurantMenu else Icons.Filled.CalendarMonth,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.RecipeList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.RecipeList.route) {
                RecipeListScreen(
                    viewModel = recipeViewModel,
                    onRecipeClick = { id -> navController.navigate("recipe_detail/$id") },
                    onAddClick = { navController.navigate("recipe_form") }
                )
            }
            composable(
                route = "recipe_detail/{recipeId}",
                arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: -1
                RecipeDetailScreen(
                    recipeId = recipeId,
                    viewModel = recipeViewModel,
                    onEditClick = { navController.navigate("recipe_form?recipeId=$recipeId") },
                    onDeleteDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "recipe_form?recipeId={recipeId}",
                arguments = listOf(navArgument("recipeId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: -1
                RecipeFormScreen(
                    recipeId = if (recipeId == -1) null else recipeId,
                    viewModel = recipeViewModel,
                    onSaveDone = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Destination.MealPlanner.route) {
                MealPlannerScreen(viewModel = mealPlannerViewModel)
            }
        }
    }
}

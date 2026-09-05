package com.example.recipemealplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.recipemealplanner.ui.navigation.AppNavigation
import com.example.recipemealplanner.ui.theme.RecipeMealPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeMealPlannerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(app = application as RecipeMealPlannerApp)
                }
            }
        }
    }
}

package com.example.recipemealplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val instructions: String,
    val prepTimeMinutes: Int,
    val servings: Int,
    val imageUri: String? = null,
    val isFavorite: Boolean = false,
    val lastViewedAt: Long? = null
)

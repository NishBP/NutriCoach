package com.fit2081.nishal34715231.data

import com.google.gson.annotations.SerializedName

data class Fruit(
    val name: String,
    val id: Int,
    val family: String,
    val genus: String,
    val order: String,
    val nutritions: Nutritions
)

data class Nutritions(
    val carbohydrates: Float,
    val protein: Float,
    val fat: Float,
    val calories: Float,
    val sugar: Float
)
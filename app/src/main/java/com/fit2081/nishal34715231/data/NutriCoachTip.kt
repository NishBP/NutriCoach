package com.fit2081.nishal34715231.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "nutricoach_tips")
data class NutriCoachTip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val tipContent: String,
    val tipType: String, // "MOTIVATION" or "FOOD_TIP"
    val createdAt: Date = Date()
)
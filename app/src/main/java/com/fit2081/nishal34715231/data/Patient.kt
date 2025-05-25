// File: app/src/main/java/com/fit2081/nishal34715231/data/Patient.kt
package com.fit2081.nishal34715231.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val userId: String,
    var phoneNumber: String?,
    var name: String?, // Name will be null initially from CSV, set on account claim
    val sex: String?,

    // HEIFA scores and other nutritional scores
    val heifaTotalScore: Float?,
    val fruitScore: Float?,
    val vegScore: Float?,
    val grainScore: Float?,
    val dairyScore: Float?,
    val meatFishPoultryScore: Float?,
    val fatsOilsScore: Float?,
    val waterScore: Float?,
    // dietVarietyScore removed as it's not in CSV
    val addedSugarScore: Float?,
    val alcoholScore: Float?,
    // outsideHomeScore removed as it's not in CSV

    // Fields for Assignment 3 Login System
    var password: String? = null,
    var isAccountClaimed: Boolean = false
)
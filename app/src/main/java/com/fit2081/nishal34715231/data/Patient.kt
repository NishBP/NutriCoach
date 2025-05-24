package com.fit2081.nishal34715231.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the 'patients' table in the Room database.
 * Each instance of this class is a row in the table.
 */
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val userId: String, // Unique identifier for the patient
    val phoneNumber: String?, // Patient's phone number
    val name: String?, // Patient's name
    val sex: String?, // Patient's sex (e.g., "Male", "Female")

    // HEIFA scores and other nutritional scores
    val heifaTotalScore: Float?,
    val fruitScore: Float?,
    val vegScore: Float?,
    val grainScore: Float?,
    val dairyScore: Float?,
    val meatFishPoultryScore: Float?,
    val fatsOilsScore: Float?,
    val waterScore: Float?,
    val dietVarietyScore: Float?,
    val addedSugarScore: Float?,
    val alcoholScore: Float?,
    val outsideHomeScore: Float?
)



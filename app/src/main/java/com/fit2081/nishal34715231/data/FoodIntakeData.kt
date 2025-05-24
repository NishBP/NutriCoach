package com.fit2081.nishal34715231.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents the 'food_intake' table in the Room database.
 * Stores questionnaire responses for a patient.
 * It has a foreign key relationship with the 'patients' table.
 */
@Entity(
    tableName = "food_intake_data",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"], // Column in the Patient table
        childColumns = ["patientUserId"], // Column in this FoodIntakeData table
        onDelete = ForeignKey.CASCADE // If a Patient is deleted, their FoodIntakeData is also deleted
    )],
    indices = [Index(value = ["patientUserId"])] // Index on patientUserId for faster queries
)
data class FoodIntakeData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated unique ID for each entry
    val patientUserId: String, // Foreign key linking to Patient.userId

    // Questionnaire responses
    val foodCategories: List<String>?, // Stored as JSON string, converted by TypeConverter
    val persona: String?,
    val breakfastTime: String?,
    val lunchTime: String?,
    val dinnerTime: String?
)
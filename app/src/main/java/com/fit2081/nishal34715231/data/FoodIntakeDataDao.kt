package com.fit2081.nishal34715231.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the FoodIntakeData entity.
 * Defines methods for interacting with the 'food_intake_data' table.
 */
@Dao
interface FoodIntakeDataDao {

    /**
     * Inserts a food intake record into the database.
     * If a record with the same ID already exists (though unlikely with autoGenerate), it's replaced.
     * More practically, you might query by patientUserId to see if data exists before inserting
     * or use an upsert (insert or update) strategy if IDs were not auto-generated.
     * @param foodIntakeData The FoodIntakeData object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntakeData(foodIntakeData: FoodIntakeData)

    /**
     * Retrieves food intake data for a specific patient.
     * Since a patient might have multiple entries over time (though not in current spec),
     * this example gets the most recent one or a specific one.
     * For this assignment, we assume one questionnaire per user.
     * If multiple entries for a user are possible, you'd need a way to distinguish them
     * (e.g., a timestamp and query for the latest).
     * This query assumes there's at most one entry per patientUserId or you want the first one found.
     * @param patientUserId The ID of the patient whose food intake data is to be retrieved.
     * @return A Flow emitting the FoodIntakeData object, or null if not found.
     */
    @Query("SELECT * FROM food_intake_data WHERE patientUserId = :patientUserId LIMIT 1")
    fun getFoodIntakeDataByUserId(patientUserId: String): Flow<FoodIntakeData?>

    /**
     * Updates an existing food intake record.
     * @param foodIntakeData The FoodIntakeData object with updated information.
     */
    @Update
    suspend fun updateFoodIntakeData(foodIntakeData: FoodIntakeData)

    /**
     * Deletes food intake data for a specific patient.
     * @param patientUserId The ID of the patient whose food intake data is to be deleted.
     */
    @Query("DELETE FROM food_intake_data WHERE patientUserId = :patientUserId")
    suspend fun deleteFoodIntakeDataByUserId(patientUserId: String)

    /**
     * Deletes all food intake data from the table.
     */
    @Query("DELETE FROM food_intake_data")
    suspend fun deleteAllFoodIntakeData()

    /**
     * Retrieves food intake data for a specific patient.
     * This is a non-Flow version, useful for one-time fetches if reactivity isn't needed.
     * @param patientUserId The ID of the patient.
     * @return The FoodIntakeData object, or null if not found.
     */
    @Query("SELECT * FROM food_intake_data WHERE patientUserId = :patientUserId LIMIT 1")
    suspend fun getFoodIntakeDataByUserIdOnce(patientUserId: String): FoodIntakeData?
}
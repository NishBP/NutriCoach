package com.fit2081.nishal34715231.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow // For reactive updates

/**
 * Data Access Object (DAO) for the Patient entity.
 * Defines methods for interacting with the 'patients' table in the database.
 * Room will generate the implementation for these methods.
 */
@Dao
interface PatientDao {

    /**
     * Inserts a single patient into the database.
     * If a patient with the same userId already exists, it will be replaced.
     * This is a suspend function, meaning it should be called from a coroutine
     * to perform the database operation off the main thread.
     * @param patient The Patient object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    /**
     * Inserts a list of patients into the database.
     * If any patient in the list has a userId that already exists, it will be replaced.
     * Useful for batch inserting data, like from the CSV.
     * @param patients A list of Patient objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPatients(patients: List<Patient>)

    /**
     * Retrieves a specific patient by their userId.
     * Returns a Flow, which allows observing changes to the patient data reactively.
     * If the patient data updates, the Flow will emit the new data.
     * @param userId The ID of the patient to retrieve.
     * @return A Flow emitting the Patient object, or null if not found.
     */
    @Query("SELECT * FROM patients WHERE userId = :userId")
    fun getPatientById(userId: String): Flow<Patient?>

    /**
     * Retrieves all patients from the database.
     * Returns a Flow, allowing observation of the entire list of patients.
     * @return A Flow emitting a list of all Patient objects.
     */
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    /**
     * Updates an existing patient in the database.
     * @param patient The Patient object with updated information.
     */
    @Update
    suspend fun updatePatient(patient: Patient)

    /**
     * Deletes a specific patient from the database by their userId.
     * @param userId The ID of the patient to delete.
     */
    @Query("DELETE FROM patients WHERE userId = :userId")
    suspend fun deletePatientById(userId: String)

    /**
     * Deletes all patients from the database.
     * Useful for clearing data if needed (e.g., for testing or reset).
     */
    @Query("DELETE FROM patients")
    suspend fun deleteAllPatients()

    /**
     * Counts the number of patients in the database.
     * This can be used to check if the database has been populated (e.g., after CSV import).
     * @return The total number of patients.
     */
    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int
}

